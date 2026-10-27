package com.redxela.api.snapshot;

import com.redxela.api.order.OrderView;
import com.redxela.api.reception.ReceptionView;
import com.redxela.api.resource.ResourceView;
import com.redxela.api.simulation.ControlsView;
import com.redxela.deadlock.DeadlockReport;
import com.redxela.stats.PolicyStats;
import com.redxela.warehouse.SlotState;
import java.util.List;

/*
 * The complete state of the simulation at one instant. The server pushes one
 * of these every few hundred milliseconds and the interface only draws it,
 * so it never has to merge partial updates. "deadlock" is null when there is
 * no conflict.
 */
public record Snapshot(double clockMinutes, double speed, ControlsView controls, CountersView counters,
                       List<ResourceView> resources, List<OrderView> inProcess, WaitingView waiting,
                       List<OrderView> completed, DeadlockReport deadlock, List<PolicyStats> stats,
                       List<StockView> stock, List<SlotState> warehouse, ReceptionView reception) {
}