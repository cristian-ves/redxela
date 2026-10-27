package com.redxela.scheduling.policy;

import com.redxela.stats.StatsCollector;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;

/*
 * Holds the active scheduling policy. Changing it also tells the statistics
 * which policy is being measured from now on, so every policy gets its own
 * results.
 */
@RequiredArgsConstructor
public class PolicySelector {

    private final StatsCollector stats;
    private final AtomicReference<PolicyType> current = new AtomicReference<>(PolicyType.STRICT_PRIORITY);

    public PolicyType current() {
        return current.get();
    }

    public void select(PolicyType type) {
        current.set(type);
        stats.switchPolicy(type.name());
    }
}