package com.redxela.inventory;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.exception.CapacityExceededException;
import com.redxela.reception.ReceptionArea;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Simulated supplier. Because the order generator never stops, stock would run
 * out and every order would end up waiting for it. This thread looks at the
 * stock every few simulated minutes and, for each product that is running low,
 * sends a truck through the normal Reception to Inventory path. It can be
 * switched off from the interface to study what happens when stock runs out,
 * and a product is not requested again until a cooldown has passed, which
 * leaves time for the previous truck to be unloaded and processed.
 */
@Slf4j
@RequiredArgsConstructor
public class AutoReplenisher {

    private static final int LOW_STOCK_UNITS = 20;
    private static final int TRUCK_UNITS = 50;
    private static final double CHECK_INTERVAL_MINUTES = 5.0;
    private static final double COOLDOWN_MINUTES = 30.0;

    private final Inventory inventory;
    private final ReceptionArea reception;
    private final SimulationClock clock;
    private final Map<String, Double> lastRequestAt = new HashMap<>();
    private volatile boolean enabled = true;
    private Thread thread;

    public void start() {
        thread = new Thread(this::watchStock, "auto-replenisher");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void watchStock() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                clock.sleepMinutes(CHECK_INTERVAL_MINUTES);
                if (enabled) {
                    replenishLowProducts();
                }
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void replenishLowProducts() {
        double now = clock.nowMinutes();
        for (StockLevel level : inventory.levels()) {
            boolean low = level.available() < LOW_STOCK_UNITS;
            boolean cooledDown = now - lastRequestAt.getOrDefault(level.sku(), -COOLDOWN_MINUTES) >= COOLDOWN_MINUTES;
            if (low && cooledDown) {
                requestTruck(level.sku(), now);
            }
        }
    }

    private void requestTruck(String sku, double now) {
        try {
            reception.register(sku, TRUCK_UNITS);
            lastRequestAt.put(sku, now);
        } catch (CapacityExceededException queueFull) {
            log.debug("No truck requested for {}: {}", sku, queueFull.getMessage());
        }
    }
}