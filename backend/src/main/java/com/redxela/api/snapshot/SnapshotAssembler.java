package com.redxela.api.snapshot;

import com.redxela.api.order.OrderView;
import com.redxela.api.order.OrderViewMapper;
import com.redxela.api.reception.ReceptionViewMapper;
import com.redxela.api.resource.ResourceViewMapper;
import com.redxela.api.simulation.ControlsViewMapper;
import com.redxela.clock.SimulationClock;
import com.redxela.deadlock.DeadlockMonitor;
import com.redxela.inventory.Inventory;
import com.redxela.inventory.StockLevel;
import com.redxela.scheduling.OrderBook;
import com.redxela.scheduling.WaitingList;
import com.redxela.scheduling.policy.PolicySelector;
import com.redxela.scheduling.policy.PolicyType;
import com.redxela.stats.StatsCollector;
import com.redxela.warehouse.Warehouse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/*
 * Builds the Snapshot by reading every component of the simulation. Nothing
 * is locked while reading: the pieces are taken one after another, so two of
 * them can differ by a few milliseconds, which is invisible on screen and
 * keeps the interface from ever slowing the simulation down.
 */
@Component
@RequiredArgsConstructor
public class SnapshotAssembler {

    private static final int WAITING_LIMIT = 100;
    private static final int COMPLETED_LIMIT = 15;

    private final SimulationClock clock;
    private final OrderBook orderBook;
    private final WaitingList waitingList;
    private final PolicySelector policy;
    private final StatsCollector stats;
    private final DeadlockMonitor deadlockMonitor;
    private final Inventory inventory;
    private final Warehouse warehouse;
    private final OrderViewMapper orders;
    private final ResourceViewMapper resources;
    private final ReceptionViewMapper reception;
    private final ControlsViewMapper controls;

    public Snapshot assemble() {
        double now = clock.nowMinutes();
        List<OrderView> inProcess = orders.toViews(orderBook.inProcess(), now);
        return new Snapshot(now, clock.speed(), controls.toView(),
                new CountersView(orderBook.submittedCount(), orderBook.completedCount(),
                        waitingList.size(), inProcess.size()),
                resources.toViews(), inProcess, waiting(now),
                orders.toViews(orderBook.recentlyCompleted(COMPLETED_LIMIT), now),
                deadlockMonitor.current(), stats.snapshot(now), stock(), warehouse.snapshot(), reception.toView());
    }

    private WaitingView waiting(double now) {
        PolicyType active = policy.current();
        List<OrderView> ordered = orders.toViews(waitingList.ordered(active, now).stream().limit(WAITING_LIMIT).toList(), now);
        return new WaitingView(active.name(), active.getLabel(), active.getCriterion(), waitingList.size(), ordered);
    }

    private List<StockView> stock() {
        return inventory.levels().stream().map(this::toStockView).toList();
    }

    private StockView toStockView(StockLevel level) {
        return new StockView(level.sku(), level.name(), level.onHand(), level.reserved(), level.available());
    }
}