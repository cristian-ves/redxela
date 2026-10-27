package com.redxela.scheduling;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.order.Order;
import com.redxela.scheduling.policy.PolicySelector;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * The scheduler thread. Every tick, and whenever something that may free a
 * waiting order happens (an order finishes, stock arrives, a new order comes
 * in), it sorts the waiting list with the active policy, asks admission
 * control which orders can start and hands them to the executor.
 *
 * It sleeps on the DispatchSignal, so a request sent while a pass is running
 * is never lost.
 */
@Slf4j
@RequiredArgsConstructor
public class Dispatcher {

    private static final long TICK_MILLIS = 100;

    private final WaitingList waitingList;
    private final AdmissionControl admission;
    private final PolicySelector policy;
    private final OrderExecutor executor;
    private final SimulationClock clock;
    private final DispatchSignal signal;
    private Thread thread;

    public void start() {
        thread = new Thread(this::loop, "dispatcher");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
        executor.shutdown();
    }

    private void loop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                signal.await(TICK_MILLIS);
                runPass();
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void runPass() {
        try {
            List<Order> ordered = waitingList.ordered(policy.current(), clock.nowMinutes());
            for (Order order : admission.admit(ordered)) {
                waitingList.remove(order);
                order.admit(clock.nowMinutes());
                executor.start(order);
            }
        } catch (RuntimeException failure) {
            log.error("Dispatch pass failed", failure);
        }
    }
}