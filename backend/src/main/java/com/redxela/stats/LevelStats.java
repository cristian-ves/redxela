package com.redxela.stats;

import com.redxela.domain.client.ServiceLevel;

/* Results for one service level under one policy. */
public record LevelStats(ServiceLevel level, int completed, double averageWaitMinutes, double withinTargetPercent) {
}