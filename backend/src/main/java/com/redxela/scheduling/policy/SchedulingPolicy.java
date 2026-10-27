package com.redxela.scheduling.policy;

import com.redxela.domain.order.Order;
import java.util.Comparator;

/*
 * Strategy that decides the order of the waiting list: the first order of the
 * comparator is the next one to be served. Every policy breaks ties by arrival
 * time and then by id, so the order is total and stable.
 */
public interface SchedulingPolicy {

    Comparator<Order> comparator(double nowMinutes);

    /* Arrival order, used as the final tie-breaker by every policy. */
    static Comparator<Order> byArrival() {
        return Comparator.comparingDouble(Order::createdAt).thenComparing(Order::id);
    }
}