package com.redxela.scheduling.policy;

import com.redxela.domain.order.Order;
import java.util.Comparator;

/*
 * Priority with aging: every AGING_STEP_MINUTES an order spends waiting it
 * moves up one service level, so the effective level keeps falling until the
 * order is served. A long-waiting Economy order eventually overtakes fresh
 * Express ones, which removes the starvation of strict priority at the cost of
 * delaying the higher tiers a little.
 */
public class PriorityWithAgingPolicy implements SchedulingPolicy {

    static final double AGING_STEP_MINUTES = 20.0;

    @Override
    public Comparator<Order> comparator(double nowMinutes) {
        return Comparator.<Order>comparingDouble(order -> effectiveLevel(order, nowMinutes))
                .thenComparing(SchedulingPolicy.byArrival());
    }

    private double effectiveLevel(Order order, double nowMinutes) {
        double waited = nowMinutes - order.createdAt();
        return order.level().getLevel() - waited / AGING_STEP_MINUTES;
    }
}