package com.redxela.api.snapshot;

import com.redxela.api.order.OrderView;
import java.util.List;

/*
 * The waiting list in the order the active policy would serve it. Only the
 * first orders are sent because the generator has no limit and the list can
 * grow very long; "total" always carries the real size.
 */
public record WaitingView(String policy, String policyLabel, String criterion, int total, List<OrderView> orders) {
}