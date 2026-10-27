package com.redxela.stats;

import com.redxela.domain.client.ServiceLevel;
import com.redxela.domain.order.Order;
import com.redxela.domain.resource.ResourceType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/*
 * Running totals for one scheduling policy. It is not thread-safe on its own:
 * StatsCollector is its only user and serializes every call.
 */
class PolicyAccumulator {

    private static final ServiceLevel[] LEVELS = ServiceLevel.values();
    private static final ResourceType[] RESOURCES = ResourceType.values();

    private final int[] completedByLevel = new int[LEVELS.length];
    private final double[] waitByLevel = new double[LEVELS.length];
    private final int[] withinTargetByLevel = new int[LEVELS.length];
    private final double[] utilizationSum = new double[RESOURCES.length];
    private double totalProcessing;
    private long utilizationSamples;
    private double activeMinutes;
    private double activeSince = Double.NaN;

    void activate(double now) {
        if (Double.isNaN(activeSince)) {
            activeSince = now;
        }
    }

    void deactivate(double now) {
        if (!Double.isNaN(activeSince)) {
            activeMinutes += now - activeSince;
            activeSince = Double.NaN;
        }
    }

    void record(Order order) {
        int level = order.level().ordinal();
        completedByLevel[level]++;
        waitByLevel[level] += order.waitedMinutes();
        totalProcessing += order.processingMinutes();
        if (order.waitedMinutes() <= order.level().getTargetWaitMinutes()) {
            withinTargetByLevel[level]++;
        }
    }

    void sampleUtilization(Map<ResourceType, Double> usedFractions) {
        for (ResourceType type : RESOURCES) {
            utilizationSum[type.ordinal()] += usedFractions.getOrDefault(type, 0.0);
        }
        utilizationSamples++;
    }

    PolicyStats snapshot(String policy, double now) {
        int completed = sum(completedByLevel);
        double elapsed = activeMinutes + (Double.isNaN(activeSince) ? 0 : now - activeSince);
        Map<ResourceType, Double> utilization = utilizationPercent();
        return new PolicyStats(policy, !Double.isNaN(activeSince), completed,
                average(sum(waitByLevel), completed), average(totalProcessing, completed),
                percent(sum(withinTargetByLevel), completed),
                elapsed > 0 ? completed / elapsed * 60 : 0,
                utilization.values().stream().mapToDouble(Double::doubleValue).average().orElse(0),
                utilization, levelStats());
    }

    private Map<ResourceType, Double> utilizationPercent() {
        Map<ResourceType, Double> percent = new EnumMap<>(ResourceType.class);
        for (ResourceType type : RESOURCES) {
            percent.put(type, utilizationSamples == 0 ? 0 : utilizationSum[type.ordinal()] / utilizationSamples * 100);
        }
        return percent;
    }

    private List<LevelStats> levelStats() {
        List<LevelStats> stats = new ArrayList<>();
        for (ServiceLevel level : LEVELS) {
            int index = level.ordinal();
            stats.add(new LevelStats(level, completedByLevel[index],
                    average(waitByLevel[index], completedByLevel[index]),
                    percent(withinTargetByLevel[index], completedByLevel[index])));
        }
        return stats;
    }

    private static int sum(int[] values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total;
    }

    private static double sum(double[] values) {
        double total = 0;
        for (double value : values) {
            total += value;
        }
        return total;
    }

    private static double average(double total, int count) {
        return count == 0 ? 0 : total / count;
    }

    private static double percent(int part, int whole) {
        return whole == 0 ? 0 : part * 100.0 / whole;
    }
}