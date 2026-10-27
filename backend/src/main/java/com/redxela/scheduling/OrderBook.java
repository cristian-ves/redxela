package com.redxela.scheduling;

import com.redxela.domain.order.Order;
import com.redxela.domain.order.OrderStatus;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/*
 * Register of every order the system knows about. Open orders (waiting or in
 * process) are kept by id; completed ones move to a bounded history, because
 * the generator has no limit and memory must not grow with it. Counters keep
 * the totals the history no longer holds.
 */
public class OrderBook {

    private static final int COMPLETED_HISTORY_SIZE = 200;

    private final Map<String, Order> openOrders = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<Order> completedHistory = new ConcurrentLinkedDeque<>();
    private final AtomicLong submitted = new AtomicLong();
    private final AtomicLong completed = new AtomicLong();

    public void register(Order order) {
        openOrders.put(order.id(), order);
        submitted.incrementAndGet();
    }

    public void complete(Order order) {
        openOrders.remove(order.id());
        completedHistory.addFirst(order);
        while (completedHistory.size() > COMPLETED_HISTORY_SIZE) {
            completedHistory.pollLast();
        }
        completed.incrementAndGet();
    }

    /* Finds an order, including recently completed ones; null when it is unknown or too old. */
    public Order find(String id) {
        Order open = openOrders.get(id);
        if (open != null) {
            return open;
        }
        return completedHistory.stream().filter(order -> order.id().equals(id)).findFirst().orElse(null);
    }

    public List<Order> inProcess() {
        return openOrders.values().stream()
                .filter(order -> order.status() == OrderStatus.PROCESSING || order.status() == OrderStatus.BLOCKED)
                .sorted(Comparator.comparing(Order::id))
                .toList();
    }

    public Collection<Order> recentlyCompleted(int limit) {
        return completedHistory.stream().limit(limit).toList();
    }

    public long submittedCount() {
        return submitted.get();
    }

    public long completedCount() {
        return completed.get();
    }
}