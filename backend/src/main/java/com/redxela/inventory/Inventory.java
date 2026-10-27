package com.redxela.inventory;

import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.order.Order;
import com.redxela.domain.order.OrderLine;
import com.redxela.domain.order.StockReservation;
import com.redxela.warehouse.Warehouse;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/*
 * Stock control. It keeps the lots of each product in arrival order, how many
 * units are on hand and how many are promised to admitted orders, and it asks
 * the warehouse for the physical slots. Orders are served first-in first-out.
 *
 * Reservations make admission safe: an order is only started when its units
 * are guaranteed, so two orders can never be promised the same stock. All
 * methods share one monitor, which keeps the counters and the lot queues
 * consistent with each other.
 */
@RequiredArgsConstructor
public class Inventory implements StockReservation {

    private final Warehouse warehouse;
    private final ProductCatalog catalog;
    private final Map<String, Deque<String>> lotsBySku = new HashMap<>();
    private final Map<String, Integer> onHandBySku = new HashMap<>();
    private final Map<String, Integer> reservedBySku = new HashMap<>();
    private final Set<String> reservedOrders = new HashSet<>();
    private int lotSequence;

    /* Puts a received lot away. Fails with CapacityExceededException if the warehouse cannot hold it. */
    public synchronized StoredLot receive(String sku, int units) {
        catalog.find(sku);
        String lotId = String.format("LOT-%04d", ++lotSequence);
        List<Integer> slots = warehouse.store(lotId, sku, units);
        lotsBySku.computeIfAbsent(sku, key -> new ArrayDeque<>()).addLast(lotId);
        onHandBySku.merge(sku, units, Integer::sum);
        return new StoredLot(lotId, sku, units, slots);
    }

    @Override
    public synchronized boolean isAvailable(Order order) {
        return order.lines().stream().allMatch(line -> availableUnits(line.product().sku()) >= line.units());
    }

    @Override
    public synchronized boolean tryReserve(Order order) {
        if (!isAvailable(order)) {
            return false;
        }
        order.lines().forEach(line -> reservedBySku.merge(line.product().sku(), line.units(), Integer::sum));
        reservedOrders.add(order.id());
        return true;
    }

    @Override
    public synchronized void release(Order order) {
        if (reservedOrders.remove(order.id())) {
            order.lines().forEach(line -> reservedBySku.merge(line.product().sku(), -line.units(), Integer::sum));
        }
    }

    /* The order is done: its reserved units leave the stock and the emptied slots are freed. */
    @Override
    public synchronized void consume(Order order) {
        release(order);
        order.lines().forEach(this::takeFromLots);
    }

    public synchronized List<StockLevel> levels() {
        return catalog.all().stream()
                .map(product -> new StockLevel(product.sku(), product.name(),
                        onHandBySku.getOrDefault(product.sku(), 0),
                        reservedBySku.getOrDefault(product.sku(), 0)))
                .toList();
    }

    private int availableUnits(String sku) {
        return onHandBySku.getOrDefault(sku, 0) - reservedBySku.getOrDefault(sku, 0);
    }

    private void takeFromLots(OrderLine line) {
        String sku = line.product().sku();
        Deque<String> lots = lotsBySku.get(sku);
        int remaining = line.units();
        while (remaining > 0) {
            String lotId = lots.peekFirst();
            int taken = Math.min(remaining, warehouse.unitsOf(lotId));
            warehouse.withdraw(lotId, taken);
            remaining -= taken;
            if (warehouse.unitsOf(lotId) == 0) {
                lots.pollFirst();
            }
        }
        onHandBySku.merge(sku, -line.units(), Integer::sum);
    }
}