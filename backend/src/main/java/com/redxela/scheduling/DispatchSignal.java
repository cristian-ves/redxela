package com.redxela.scheduling;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/*
 * Wake-up line to the dispatcher. Whatever may let a waiting order start (a
 * new order, a finished order, new stock) calls request(); the dispatcher
 * sleeps on await(). Requests are counted on a semaphore, so one sent while a
 * pass is running is not lost, and many requests collapse into a single pass.
 * It also breaks the circular dependency between the dispatcher and the
 * components that need to wake it.
 */
public class DispatchSignal {

    private final Semaphore requests = new Semaphore(0);

    public void request() {
        requests.release();
    }

    /* Waits for a request or until the timeout, then discards the pending ones. */
    public void await(long timeoutMillis) throws InterruptedException {
        requests.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        requests.drainPermits();
    }
}