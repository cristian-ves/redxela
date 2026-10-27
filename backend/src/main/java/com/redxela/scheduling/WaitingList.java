package com.redxela.scheduling;

import com.redxela.domain.order.Order;
import com.redxela.scheduling.policy.PolicyType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Orders that have not started yet. The list itself is unordered storage; the
 * order shown to the user and followed by the dispatcher is produced on demand
 * by the active policy, so switching policy reorders the whole list at once.
 */
public class WaitingList {

    private final Map<String, Order> ordersById = new ConcurrentHashMap<>();

    public void add(Order order) {
        ordersById.put(order.id(), order);
    }

    public void remove(Order order) {
        ordersById.remove(order.id());
    }

    public int size() {
        return ordersById.size();
    }

    public List<Order> ordered(PolicyType policy, double nowMinutes) {
        return ordersById.values().stream()
                .sorted(policy.getPolicy().comparator(nowMinutes))
                .toList();
    }
}