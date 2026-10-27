package com.redxela.stats;

import com.redxela.domain.resource.ResourceType;
import java.util.List;
import java.util.Map;

/*
 * Everything measured while one scheduling policy was active. Times are in
 * simulated minutes; throughput is completed orders per simulated hour of busy
 * time, and utilization is averaged over that same busy time, so runs of
 * different length (or with idle gaps) can be compared.
 */
public record PolicyStats(String policy, boolean active, int completed,
                          double averageWaitMinutes, double averageProcessingMinutes,
                          double withinTargetPercent, double throughputPerHour,
                          double averageUtilizationPercent,
                          Map<ResourceType, Double> utilizationPercent, List<LevelStats> levels) {
}
