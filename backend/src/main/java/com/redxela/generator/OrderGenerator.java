package com.redxela.generator;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.exception.InvalidRequestException;
import com.redxela.scheduling.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Automatic order generator. It has no limit on the number of orders: it
 * keeps producing them at the configured rate until it is stopped, which is
 * how the waiting list can grow without bound when the system cannot keep up.
 */
@Slf4j
@RequiredArgsConstructor
public class OrderGenerator {

    private static final double DEFAULT_ORDERS_PER_MINUTE = 0.4;
    private static final double MAX_ORDERS_PER_MINUTE = 5.0;

    private final RandomOrderSource source;
    private final OrderService orders;
    private final SimulationClock clock;
    private volatile double ordersPerMinute = DEFAULT_ORDERS_PER_MINUTE;
    private Long seed;
    private Thread thread;

    /* Starts generating. The seed, if given, restarts the random sequence so runs can be repeated. */
    public synchronized void start(Long newSeed) {
        if (isRunning()) {
            return;
        }
        seed = newSeed;
        source.reseed(newSeed);
        thread = new Thread(this::generate, "order-generator");
        thread.start();
    }

    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public synchronized boolean isRunning() {
        return thread != null;
    }

    public synchronized Long seed() {
        return seed;
    }

    public double ordersPerMinute() {
        return ordersPerMinute;
    }

    public void setOrdersPerMinute(double rate) {
        if (rate <= 0 || rate > MAX_ORDERS_PER_MINUTE) {
            throw new InvalidRequestException("The rate must be above 0 and at most " + MAX_ORDERS_PER_MINUTE);
        }
        this.ordersPerMinute = rate;
    }

    private void generate() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                clock.sleepMinutes(source.nextGapMinutes(ordersPerMinute));
                submitNext();
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void submitNext() {
        try {
            orders.submit(source.nextRequest());
        } catch (RuntimeException failure) {
            log.error("The generator could not create an order", failure);
        }
    }
}