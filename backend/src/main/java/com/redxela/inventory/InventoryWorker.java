package com.redxela.inventory;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.exception.CapacityExceededException;
import com.redxela.messaging.ArrivalMessage;
import com.redxela.messaging.BoundedBuffer;
import com.redxela.warehouse.SlotLocation;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Inventory control area: the consumer side of the link. It takes messages at
 * its own pace (three simulated minutes each), updates the stock, assigns the
 * warehouse locations and then tells the scheduler that stock changed, so the
 * orders that were waiting for that product are evaluated again.
 */
@Slf4j
@RequiredArgsConstructor
public class InventoryWorker {

    private static final double PROCESSING_MINUTES_PER_MESSAGE = 3.0;
    private static final int HISTORY_SIZE = 20;

    private final BoundedBuffer<ArrivalMessage> inbox;
    private final Inventory inventory;
    private final SimulationClock clock;
    private final Runnable onStockChanged;
    private final ConcurrentLinkedDeque<ProcessedArrival> recent = new ConcurrentLinkedDeque<>();
    private Thread thread;

    public void start() {
        thread = new Thread(this::consume, "inventory-control");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public List<ProcessedArrival> recent() {
        return List.copyOf(recent);
    }

    private void consume() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ArrivalMessage message = inbox.take();
                clock.sleepMinutes(PROCESSING_MINUTES_PER_MESSAGE);
                handle(message);
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void handle(ArrivalMessage message) {
        try {
            StoredLot lot = inventory.receive(message.sku(), message.units());
            List<String> locations = lot.slots().stream().map(slot -> SlotLocation.fromIndex(slot).label()).toList();
            remember(new ProcessedArrival(message.receptionId(), message.sku(), message.units(),
                    lot.lotId(), locations, "STORED"));
            onStockChanged.run();
        } catch (CapacityExceededException full) {
            log.warn("Reception {} rejected: {}", message.receptionId(), full.getMessage());
            remember(new ProcessedArrival(message.receptionId(), message.sku(), message.units(),
                    null, List.of(), "REJECTED: " + full.getMessage()));
        } catch (RuntimeException failure) {
            log.error("Inventory failed to process {}", message, failure);
        }
    }

    private void remember(ProcessedArrival arrival) {
        recent.addFirst(arrival);
        while (recent.size() > HISTORY_SIZE) {
            recent.pollLast();
        }
    }
}