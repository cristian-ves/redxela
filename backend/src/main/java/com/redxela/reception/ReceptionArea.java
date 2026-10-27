package com.redxela.reception;

import com.redxela.domain.catalog.Product;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.exception.CapacityExceededException;
import com.redxela.domain.exception.InvalidRequestException;
import com.redxela.messaging.ArrivalMessage;
import com.redxela.messaging.BoundedBuffer;
import com.redxela.resources.OperationRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Reception area: the producer side of the Reception to Inventory link. Trucks
 * registered from the interface wait in an arrival queue; a single dock thread
 * unloads them one at a time (forklift and scanner) and sends an ArrivalMessage
 * through the bounded buffer. When Inventory falls behind, the buffer fills up
 * and the dock thread waits there, so trucks queue up instead of data being
 * lost. The arrival queue is bounded too: registration is refused when it is
 * full rather than growing without limit.
 */
@Slf4j
@RequiredArgsConstructor
public class ReceptionArea {

    private static final int ARRIVAL_QUEUE_CAPACITY = 50;
    private static final int MAX_UNITS_PER_TRUCK = 200;
    private static final int HISTORY_SIZE = 20;

    private final ProductCatalog catalog;
    private final OperationRunner runner;
    private final BoundedBuffer<ArrivalMessage> outbox;
    private final BlockingQueue<ReceptionOperation> arrivals = new ArrayBlockingQueue<>(ARRIVAL_QUEUE_CAPACITY);
    private final ConcurrentLinkedDeque<ReceptionOperation> recent = new ConcurrentLinkedDeque<>();
    private final AtomicInteger sequence = new AtomicInteger();
    private volatile ReceptionOperation current;
    private Thread dockThread;

    public ReceptionOperation register(String sku, int units) {
        Product product = catalog.find(sku);
        if (units < 1 || units > MAX_UNITS_PER_TRUCK) {
            throw new InvalidRequestException("A truck carries between 1 and " + MAX_UNITS_PER_TRUCK + " units");
        }
        ReceptionOperation truck = new ReceptionOperation(
                String.format("RCV-%04d", sequence.incrementAndGet()), product, units);
        if (!arrivals.offer(truck)) {
            throw new CapacityExceededException("The reception queue is full (" + ARRIVAL_QUEUE_CAPACITY + " trucks)");
        }
        return truck;
    }

    public void start() {
        dockThread = new Thread(this::serveDock, "reception-dock");
        dockThread.start();
    }

    public void stop() {
        dockThread.interrupt();
    }

    public List<ReceptionOperation> queued() {
        return new ArrayList<>(arrivals);
    }

    public ReceptionOperation current() {
        return current;
    }

    public List<ReceptionOperation> recent() {
        return List.copyOf(recent);
    }

    public int queueCapacity() {
        return ARRIVAL_QUEUE_CAPACITY;
    }

    private void serveDock() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ReceptionOperation truck = arrivals.take();
                current = truck;
                unload(truck);
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        } finally {
            current = null;
        }
    }

    private void unload(ReceptionOperation truck) throws InterruptedException {
        try {
            truck.startReceiving();
            runner.run(truck);
            truck.waitForInventory();
            outbox.put(new ArrivalMessage(truck.id(), truck.product().sku(), truck.units()));
            truck.markSent();
            remember(truck);
        } catch (RuntimeException failure) {
            log.error("Reception {} failed", truck.id(), failure);
        }
    }

    private void remember(ReceptionOperation truck) {
        recent.addFirst(truck);
        while (recent.size() > HISTORY_SIZE) {
            recent.pollLast();
        }
    }
}