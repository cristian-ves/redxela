package com.redxela.domain.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * Service tiers a client can contract. The tier decides how soon an order is
 * taken from the waiting list; the target is the longest wait, in simulated
 * minutes, promised to the client. Express is "almost instant", so it is
 * measured against one minute.
 */
@Getter
@RequiredArgsConstructor
public enum ServiceLevel {
    EXPRESS(1, "Express", 1),
    PRIORITY(2, "Priority", 10),
    STANDARD(3, "Standard", 30),
    SCHEDULED(4, "Scheduled", 60),
    ECONOMY(5, "Economy", 120);

    private final int level;
    private final String label;
    private final int targetWaitMinutes;
}
