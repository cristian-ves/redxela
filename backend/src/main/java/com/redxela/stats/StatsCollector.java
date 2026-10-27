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

    public synchronized void switchPolicy(String policy, double now) {
        if (activePolicy != null) {
            accumulators.get(activePolicy).deactivate(now);
        }
        register(policy);
        activePolicy = policy;
        accumulators.get(policy).activate(now);
    }

    public synchronized void recordCompletion(Order order) {
        accumulators.get(activePolicy).record(order);
    }

    public synchronized void sampleUtilization(Map<ResourceType, Double> usedFractions) {
        accumulators.get(activePolicy).sampleUtilization(usedFractions);
    }

    /* Discards every measurement and starts counting again from now under the same policy. */
    public synchronized void reset(double now) {
        accumulators.replaceAll((policy, old) -> new PolicyAccumulator());
        accumulators.get(activePolicy).activate(now);
    }

    public synchronized List<PolicyStats> snapshot(double now) {
        return accumulators.entrySet().stream()
                .map(entry -> entry.getValue().snapshot(entry.getKey(), now))
                .toList();
    }
}