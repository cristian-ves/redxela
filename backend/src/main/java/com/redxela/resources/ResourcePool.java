package com.redxela.resources;

import com.redxela.domain.resource.ResourceType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/*
 * A pool of identical units (packing stations, forklifts, ...) guarded by a
 * fair counting semaphore. Besides blocking the callers, the pool records who
 * holds how many units, who is waiting for how many and which units are
 * reserved for an admitted order that has not requested them yet. Holders and
 * waiters feed the interface and the deadlock detector; reservations let the
 * scheduler admit an order only when everything it needs will be there.
 *
 * Reads of those maps are lock-free snapshots: they may be a few microseconds
 * old, which is why the deadlock detector confirms its findings before acting.
 */
public class ResourcePool {

    private final ResourceType type;
    private final Semaphore permits;
    private final Map<String, Integer> holders = new ConcurrentHashMap<>();
    private final Map<String, Integer> waiters = new ConcurrentHashMap<>();
    private final Map<String, Integer> reservations = new ConcurrentHashMap<>();

    public ResourcePool(ResourceType type) {
        this.type = type;
        this.permits = new Semaphore(type.getCapacity(), true);
    }

    public ResourceType type() {
        return type;
    }

    public int capacity() {
        return type.getCapacity();
    }

    public int available() {
        return permits.availablePermits();
    }

    /* Units that are free and not promised to an admitted order. */
    public int unreserved() {
        return available() - reservations.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void reserve(String owner, int units) {
        reservations.merge(owner, units, Integer::sum);
    }

    /*
     * Takes the units, blocking if they are not free. The fair semaphore queues
     * waiters in arrival order and the zero-timeout attempt respects that queue,
     * so a request for several units cannot be overtaken forever by small ones.
     * onBlocked runs only when the caller has to wait while holding other units.
     */
    public void acquire(String owner, int units, Runnable onBlocked) throws InterruptedException {
        waiters.put(owner, units);
        try {
            if (!permits.tryAcquire(units, 0, TimeUnit.MILLISECONDS)) {
                onBlocked.run();
                permits.acquire(units);
            }
            holders.merge(owner, units, Integer::sum);
            reservations.computeIfPresent(owner, (key, reserved) -> reserved > units ? reserved - units : null);
        } finally {
            waiters.remove(owner);
        }
    }

    /* Returns everything the owner holds and cancels its reservations. */
    public void releaseAll(String owner) {
        reservations.remove(owner);
        Integer held = holders.remove(owner);
        if (held != null) {
            permits.release(held);
        }
    }

    public Map<String, Integer> holders() {
        return Map.copyOf(holders);
    }

    public Map<String, Integer> waiters() {
        return Map.copyOf(waiters);
    }
}