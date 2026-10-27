package com.redxela.scheduling.policy;

import com.redxela.domain.order.Order;
import java.util.Comparator;

/*
 * Fewest items first: the order with the least work goes first, which frees
 * stations sooner and lowers the average wait (shortest-job-first). Large
 * orders can starve if small ones keep coming, and the service level only
 * breaks ties between orders of the same size.
 */
public class ShortestOrderFirstPolicy implements SchedulingPolicy {

    @Override
    public Comparator<Order> comparator(double nowMinutes) {
        return Comparator.<Order>comparingInt(Order::totalUnits)
                .thenComparingInt(order -> order.level().getLevel())
                .thenComparing(SchedulingPolicy.byArrival());
    }
}