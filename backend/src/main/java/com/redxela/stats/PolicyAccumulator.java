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
 *
 * Throughput and utilization are measured over busy time only: the simulated
 * minutes during which at least one order was open. Otherwise a policy that
 * stays selected after the work has run out would keep losing both figures
 * just for the clock going on.
 */
class PolicyAccumulator {

    private static final ServiceLevel[] LEVELS = ServiceLevel.values();
    private static final ResourceType[] RESOURCES = ResourceType.values();

    private final int[] completedByLevel = new int[LEVELS.length];
    private final double[] waitByLevel = new double[LEVELS.length];
    private final int[] withinTargetByLevel = new int[LEVELS.length];
    private final double[] usedMinutes = new double[RESOURCES.length];
    private double totalProcessing;
    private double busyMinutes;
    private boolean active;

    void activate() {
        active = true;
    }

    void deactivate() {
        active = false;
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

    /* Adds a sample that stands for the given simulated minutes; samples taken while idle are ignored. */
    void sample(Map<ResourceType, Double> usedFractions, double minutes, boolean busy) {
        if (!busy) {
            return;
        }
        for (ResourceType type : RESOURCES) {
            usedMinutes[type.ordinal()] += usedFractions.getOrDefault(type, 0.0) * minutes;
        }
        busyMinutes += minutes;
    }

    PolicyStats snapshot(String policy) {
        int completed = sum(completedByLevel);
        Map<ResourceType, Double> utilization = utilizationPercent();
        return new PolicyStats(policy, active, completed,
                average(sum(waitByLevel), completed), average(totalProcessing, completed),
                percent(sum(withinTargetByLevel), completed),
                busyMinutes > 0 ? completed / busyMinutes * 60 : 0,
                utilization.values().stream().mapToDouble(Double::doubleValue).average().orElse(0),
                utilization, levelStats());
    }

    private Map<ResourceType, Double> utilizationPercent() {
        Map<ResourceType, Double> percent = new EnumMap<>(ResourceType.class);
        for (ResourceType type : RESOURCES) {
            percent.put(type, busyMinutes == 0 ? 0 : usedMinutes[type.ordinal()] / busyMinutes * 100);
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
