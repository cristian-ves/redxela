package com.redxela.clock;

import com.redxela.domain.exception.InvalidRequestException;

/*
 * Simulated time. At speed 1 one real second is one simulated minute; the
 * speed can be changed while the simulation runs. Time is kept as a base
 * value plus the real time elapsed since the last speed change, so changing
 * the speed never makes time jump.
 */
public class SimulationClock {

    private static final long SLEEP_SLICE_MILLIS = 50;
    private static final double MAX_SPEED = 20;
    private static final double NANOS_PER_SIMULATED_MINUTE = 1_000_000_000.0;

    private double baseMinutes;
    private long baseNanos = System.nanoTime();
    private double speed = 1.0;

    public synchronized double nowMinutes() {
        return baseMinutes + (System.nanoTime() - baseNanos) / NANOS_PER_SIMULATED_MINUTE * speed;
    }

    public synchronized double speed() {
        return speed;
    }

    public synchronized void setSpeed(double newSpeed) {
        if (newSpeed <= 0 || newSpeed > MAX_SPEED) {
            throw new InvalidRequestException("Speed must be above 0 and at most " + (int) MAX_SPEED);
        }
        baseMinutes = nowMinutes();
        baseNanos = System.nanoTime();
        speed = newSpeed;
    }

    /* Blocks the calling thread until the given number of simulated minutes has passed. */
    public void sleepMinutes(double minutes) throws InterruptedException {
        double target = nowMinutes() + minutes;
        while (nowMinutes() < target) {
            Thread.sleep(SLEEP_SLICE_MILLIS);
        }
    }
}