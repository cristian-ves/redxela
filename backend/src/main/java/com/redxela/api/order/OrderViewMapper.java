package com.redxela.api.order;

import com.redxela.api.order.OrderView.LineView;
import com.redxela.clock.SimulationClock;
import com.redxela.domain.order.Order;
import com.redxela.domain.order.OrderStatus;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/*
 * Turns the live Order objects into immutable views. Orders are mutated by
 * other threads while this runs, so every field is read once and the view is
 * a consistent-enough copy; it never changes after it is built.
 */
@Component
@RequiredArgsConstructor
public class OrderViewMapper {

    private final SimulationClock clock;

    public OrderView toView(Order order) {
        return toView(order, clock.nowMinutes());
    }

    public List<OrderView> toViews(Collection<Order> orders, double now) {
        return orders.stream().map(order -> toView(order, now)).toList();
    }

    public OrderView toView(Order order, double now) {
        OrderStatus status = order.status();
        boolean waiting = status == OrderStatus.WAITING;
        return new OrderView(order.id(), order.client().id(), order.client().name(),
                order.level().getLevel(), order.level().getLabel(),
                order.type().name(), order.type().getLabel(), lines(order), order.totalUnits(), order.immediate(),
                status.name(),
                waiting ? order.waitReason().name() : null,
                waiting ? order.waitReason().getLabel() : null,
                status == OrderStatus.BLOCKED && order.waitingFor() != null ? order.waitingFor().name() : null,
                order.heldResources().stream().map(Enum::name).toList(),
                order.progress(), order.remainingMinutes(), order.createdAt(),
                waitedMinutes(order, status, now),
                status == OrderStatus.COMPLETED ? order.processingMinutes() : 0,
                waiting ? order.deferredUntil() : null);
    }

    /* A waiting order is still waiting, so its wait is measured up to now; otherwise up to when it started. */
    private double waitedMinutes(Order order, OrderStatus status, double now) {
        boolean notStarted = status == OrderStatus.WAITING || Double.isNaN(order.startedAt());
        return notStarted ? now - order.createdAt() : order.startedAt() - order.createdAt();
    }

    private List<LineView> lines(Order order) {
        return order.lines().stream()
                .map(line -> new LineView(line.product().sku(), line.product().name(), line.units()))
                .toList();
    }
}