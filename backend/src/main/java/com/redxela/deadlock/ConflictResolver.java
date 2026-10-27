package com.redxela.deadlock;

import com.redxela.domain.exception.InvalidRequestException;
import com.redxela.domain.order.Order;
import com.redxela.scheduling.OrderBook;
import com.redxela.scheduling.OrderExecutor;
import java.util.List;
import lombok.RequiredArgsConstructor;

/*
 * Manual deadlock resolution by rollback. The order the user picks keeps its
 * resources and continues; every other order in the deadlock is interrupted,
 * which makes it release everything it holds, and goes back to the waiting
 * list. Each loser is marked with the winner before the interrupt, so its
 * worker knows this is a rollback, and the scheduler keeps it waiting until
 * the winner has completed. Without that pause the loser would be admitted
 * again at once and rebuild the same deadlock.
 *
 * Receptions caught behind the deadlock are left alone: they continue by
 * themselves as soon as the resources they wait for are free.
 */
@RequiredArgsConstructor
public class ConflictResolver {

    private final DeadlockMonitor monitor;
    private final OrderBook orderBook;
    private final OrderExecutor executor;

    /* Returns the ids of the orders that were sent back to the waiting list. */
    public List<String> resolve(String winnerId) {
        DeadlockReport report = monitor.current();
        if (report == null) {
            throw new InvalidRequestException("There is no deadlock to resolve");
        }
        List<String> losers = losersFor(report, winnerId);
        losers.forEach(loserId -> sendBack(loserId, winnerId));
        monitor.clear();
        return losers;
    }

    private List<String> losersFor(DeadlockReport report, String winnerId) {
        boolean winnerIsInvolvedOrder = report.find(winnerId).filter(InvolvedOperation::order).isPresent();
        if (!winnerIsInvolvedOrder) {
            throw new InvalidRequestException(winnerId + " is not an order involved in the current deadlock");
        }
        List<String> losers = report.operations().stream()
                .filter(InvolvedOperation::order)
                .map(InvolvedOperation::id)
                .filter(id -> !id.equals(winnerId))
                .toList();
        if (losers.isEmpty()) {
            throw new InvalidRequestException("There is no other order to send back to the waiting list");
        }
        return losers;
    }

    private void sendBack(String loserId, String winnerId) {
        Order loser = orderBook.find(loserId);
        if (loser != null) {
            loser.deferUntil(winnerId);
            executor.interrupt(loserId);
        }
    }
}