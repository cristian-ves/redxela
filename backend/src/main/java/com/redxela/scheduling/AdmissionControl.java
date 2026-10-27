package com.redxela.scheduling;

import com.redxela.domain.order.Order;
import com.redxela.domain.order.OrderStatus;
import com.redxela.domain.order.StockReservation;
import com.redxela.domain.order.WaitReason;
import com.redxela.domain.resource.ResourceRequirement;
import com.redxela.domain.resource.ResourceType;
import com.redxela.resources.ResourceManager;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/*
 * Decides which waiting orders may start, walking the list in the order the
 * active policy gives. An order is admitted only if all the resources it needs
 * and all its stock can be reserved at that moment; otherwise it stays in the
 * list with the reason it is waiting.
 *
 * An order that cannot get its resources "contends" for them: orders behind it
 * that need any of the same resource types are held back too. Without that,
 * small orders would keep slipping past a large one and starve it, and the
 * policy would not really decide who gets contested resources first. Orders
 * that need other resources are not affected.
 *
 * Orders flagged as immediate skip the resource check on purpose. That is how
 * a deadlock is provoked from the interface.
 */
@RequiredArgsConstructor
public class AdmissionControl {

    private final ResourceManager resources;
    private final StockReservation stock;
    private final OrderBook orderBook;

    public List<Order> admit(List<Order> orderedByPolicy) {
        List<Order> admitted = new ArrayList<>();
        Set<ResourceType> contended = EnumSet.noneOf(ResourceType.class);
        for (Order order : orderedByPolicy) {
            if (isDeferred(order)) {
                order.waitFor(WaitReason.RETURNED_BY_CONFLICT);
            } else if (tryAdmit(order, contended)) {
                admitted.add(order);
            }
        }
        return admitted;
    }

    private boolean tryAdmit(Order order, Set<ResourceType> contended) {
        if (!stock.isAvailable(order)) {
            order.waitFor(WaitReason.NO_STOCK);
            return false;
        }
        if (order.immediate()) {
            return stock.tryReserve(order);
        }
        if (!reserveResources(order, contended)) {
            order.waitFor(WaitReason.NO_RESOURCES);
            return false;
        }
        if (!stock.tryReserve(order)) {
            resources.releaseAll(order.id());
            order.waitFor(WaitReason.NO_STOCK);
            return false;
        }
        return true;
    }

    private boolean reserveResources(Order order, Set<ResourceType> contended) {
        List<ResourceType> needed = order.requirements().stream().map(ResourceRequirement::type).toList();
        boolean blockedByEarlierOrder = needed.stream().anyMatch(contended::contains);
        if (blockedByEarlierOrder || !resources.tryReserve(order.id(), order.requirements())) {
            contended.addAll(needed);
            return false;
        }
        return true;
    }

    /* An order returned by a conflict resolution waits until the order that won it has completed. */
    private boolean isDeferred(Order order) {
        String winnerId = order.deferredUntil();
        if (winnerId == null) {
            return false;
        }
        Order winner = orderBook.find(winnerId);
        boolean winnerDone = winner == null || winner.status() == OrderStatus.COMPLETED;
        if (winnerDone) {
            order.deferUntil(null);
        }
        return !winnerDone;
    }
}