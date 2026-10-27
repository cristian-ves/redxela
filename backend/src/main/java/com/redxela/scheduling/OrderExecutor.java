package com.redxela.scheduling;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.order.Order;
import com.redxela.domain.order.StockReservation;
import com.redxela.resources.OperationRunner;
import com.redxela.stats.StatsCollector;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Runs every admitted order on its own thread, so many orders are processed in
 * parallel and each one can block on a resource without holding up the rest.
 * It also knows which order runs on which thread, which is what lets a
 * conflict resolution interrupt the losing orders.
 */
@Slf4j
@RequiredArgsConstructor
public class OrderExecutor {

    private final OperationRunner runner;
    private final StockReservation stock;
    private final OrderBook orderBook;
    private final WaitingList waitingList;
    private final StatsCollector stats;
    private final SimulationClock clock;
    private final Runnable onCapacityFreed;
    private final Map<String, Future<?>> running = new ConcurrentHashMap<>();
    private final ExecutorService threads = Executors.newCachedThreadPool(namedThreads());

    public void start(Order order) {
        Future<?> future = threads.submit(() -> execute(order));
        running.put(order.id(), future);
        if (future.isDone()) {
            running.remove(order.id());
        }
    }

    /* Interrupts the thread of a running order; returns false if it is not running. */
    public boolean interrupt(String orderId) {
        Future<?> future = running.get(orderId);
        return future != null && future.cancel(true);
    }

    public List<String> runningIds() {
        return List.copyOf(running.keySet());
    }

    public void shutdown() {
        threads.shutdownNow();
    }

    private void execute(Order order) {
        try {
            runner.run(order);
            stock.consume(order);
            order.complete(clock.nowMinutes());
            orderBook.complete(order);
            stats.recordCompletion(order);
        } catch (InterruptedException interrupted) {
            handleInterruption(order);
        } catch (RuntimeException failure) {
            log.error("Order {} failed and goes back to the waiting list", order.id(), failure);
            giveBack(order);
        } finally {
            running.remove(order.id());
            onCapacityFreed.run();
        }
    }

    /*
     * A conflict resolution marks the losers with the order that won before it
     * interrupts them. An interrupt without that mark is a shutdown, and the
     * order is simply abandoned.
     */
    private void handleInterruption(Order order) {
        if (order.deferredUntil() == null) {
            stock.release(order);
            Thread.currentThread().interrupt();
            return;
        }
        giveBack(order);
    }

    private void giveBack(Order order) {
        stock.release(order);
        order.returnToWaiting();
        waitingList.add(order);
    }

    private static ThreadFactory namedThreads() {
        AtomicInteger counter = new AtomicInteger();
        return task -> new Thread(task, "order-worker-" + counter.incrementAndGet());
    }
}