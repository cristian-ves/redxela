package com.redxela.stats;

import com.redxela.domain.order.Order;
import com.redxela.domain.resource.ResourceType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Keeps one set of measurements per scheduling policy, so policies can be
 * compared side by side. Only the policy that is active when an order
 * completes (or a utilization sample is taken) receives the measurement.
 * Every method is synchronized because workers, the sampler and the interface
 * all reach the collector from different threads.
 */
public class StatsCollector {

    private final Map<String, PolicyAccumulator> accumulators = new LinkedHashMap<>();
    private String activePolicy;

    public synchronized void register(String policy) {
        accumulators.computeIfAbsent(policy, key -> new PolicyAccumulator());
    }

    public synchronized void switchPolicy(String policy) {
        if (activePolicy != null) {
            accumulators.get(activePolicy).deactivate();
        }
        register(policy);
        activePolicy = policy;
        accumulators.get(policy).activate();
    }

    public synchronized void recordCompletion(Order order) {
        accumulators.get(activePolicy).record(order);
    }

    /* One utilization sample standing for the given simulated minutes; it only counts when the system had open orders. */
    public synchronized void sampleUtilization(Map<ResourceType, Double> usedFractions, double minutes, boolean busy) {
        accumulators.get(activePolicy).sample(usedFractions, minutes, busy);
    }

    /* Discards every measurement and starts counting again under the same policy. */
    public synchronized void reset() {
        accumulators.replaceAll((policy, old) -> new PolicyAccumulator());
        accumulators.get(activePolicy).activate();
    }

    public synchronized List<PolicyStats> snapshot() {
        return accumulators.entrySet().stream()
                .map(entry -> entry.getValue().snapshot(entry.getKey()))
                .toList();
    }
}
