package com.redxela.deadlock;

import com.redxela.clock.SimulationClock;
import com.redxela.resources.ResourceManager;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Background thread that looks for deadlocks twice a second. The pool maps are
 * read without a lock, so a single pass can catch an operation halfway between
 * two steps and see a cycle that is not there. A deadlock is therefore only
 * reported when two consecutive passes find exactly the same operations; a
 * real deadlock never goes away on its own, a transient one does.
 */
@Slf4j
@RequiredArgsConstructor
public class DeadlockMonitor {

    private static final long CHECK_INTERVAL_MILLIS = 500;

    private final ResourceManager resources;
    private final DeadlockDetector detector;
    private final DeadlockReporter reporter;
    private final SimulationClock clock;
    private final AtomicReference<DeadlockReport> confirmed = new AtomicReference<>();
    private volatile Set<String> suspected = Set.of();
    private Thread thread;

    public void start() {
        thread = new Thread(this::watch, "deadlock-monitor");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    /* The deadlock confirmed right now, or null when there is none. */
    public DeadlockReport current() {
        return confirmed.get();
    }

    /* Forgets the current finding, used right after a resolution so the interface does not show a stale conflict. */
    public void clear() {
        suspected = Set.of();
        confirmed.set(null);
    }

    private void watch() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                check();
                Thread.sleep(CHECK_INTERVAL_MILLIS);
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void check() {
        try {
            List<PoolState> pools = resources.pools().stream().map(PoolState::of).toList();
            update(detector.detect(pools), pools);
        } catch (RuntimeException failure) {
            log.error("Deadlock check failed", failure);
        }
    }

    private void update(Set<String> found, List<PoolState> pools) {
        if (found.isEmpty()) {
            clear();
            return;
        }
        DeadlockReport existing = confirmed.get();
        boolean alreadyReported = existing != null && existing.ids().equals(found);
        if (!alreadyReported && found.equals(suspected)) {
            confirmed.set(reporter.build(found, pools, clock.nowMinutes()));
        }
        suspected = found;
    }
}