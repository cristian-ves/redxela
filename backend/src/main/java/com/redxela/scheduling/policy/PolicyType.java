package com.redxela.scheduling.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * The scheduling policies that can be selected at run time. The criterion is
 * shown above the waiting list so the order of the list can be understood.
 */
@Getter
@RequiredArgsConstructor
public enum PolicyType {
    STRICT_PRIORITY("Strict priority",
            "Service level first, then arrival time",
            new StrictPriorityPolicy()),
    PRIORITY_WITH_AGING("Priority with aging",
            "Service level, improved by one level for every 20 minutes of waiting",
            new PriorityWithAgingPolicy()),
    FEWEST_ITEMS_FIRST("Fewest items first",
            "Smallest order first, then service level and arrival time",
            new ShortestOrderFirstPolicy());

    private final String label;
    private final String criterion;
    private final SchedulingPolicy policy;
}