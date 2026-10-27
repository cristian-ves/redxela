package com.redxela.scheduling.policy;

import com.redxela.domain.order.Order;
import java.util.Comparator;

/*
 * Strict priority: a lower service level is always served before a higher one.
 * Simple and fast for Express clients, but an Economy order can wait forever
 * while higher tiers keep arriving (starvation).
 */
public class StrictPriorityPolicy implements SchedulingPolicy {

    @Override
    public Comparator<Order> comparator(double nowMinutes) {
        return Comparator.<Order>comparingInt(order -> order.level().getLevel())
                .thenComparing(SchedulingPolicy.byArrival());
    }
}