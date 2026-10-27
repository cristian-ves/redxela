package com.redxela.domain.order;

import com.redxela.domain.catalog.MerchandiseType;
import com.redxela.domain.client.Client;
import com.redxela.domain.client.ServiceLevel;
import com.redxela.domain.resource.Operation;
import com.redxela.domain.resource.ResourceRequirement;
import com.redxela.domain.resource.ResourceType;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * A customer order. Its data is immutable; its progress is written by the
 * worker thread running it and by the dispatcher, and read by the snapshot
 * publisher, so mutable fields are volatile and multi-field transitions are
 * synchronized. The amount of work is estimated from the units it contains.
 */
public class Order implements Operation {

    public static final double WORK_MINUTES_PER_UNIT = 0.5;

    private final String id;
    private final Client client;
    private final MerchandiseType type;
    private final List<OrderLine> lines;
    private final int totalUnits;
    private final double createdAt;
    private final boolean immediate;
    private final List<ResourceType> held = new CopyOnWriteArrayList<>();

    private volatile OrderStatus status = OrderStatus.WAITING;
    private volatile WaitReason waitReason = WaitReason.NO_RESOURCES;
    private volatile double startedAt = Double.NaN;
    private volatile double completedAt = Double.NaN;
    private volatile double progress;
    private volatile double remainingMinutes;
    private volatile ResourceType waitingFor;
    private volatile String deferredUntil;

    public Order(String id, Client client, MerchandiseType type, List<OrderLine> lines,
                 double createdAt, boolean immediate) {
        this.id = id;
        this.client = client;
        this.type = type;
        this.lines = List.copyOf(lines);
        this.totalUnits = lines.stream().mapToInt(OrderLine::units).sum();
        this.createdAt = createdAt;
        this.immediate = immediate;
        this.remainingMinutes = totalUnits * WORK_MINUTES_PER_UNIT;
    }

    @Override public String id() { return id; }
    @Override public List<ResourceRequirement> requirements() { return type.getRequirements(); }
    @Override public double workMinutes() { return totalUnits * WORK_MINUTES_PER_UNIT; }

    public Client client() { return client; }
    public ServiceLevel level() { return client.level(); }
    public MerchandiseType type() { return type; }
    public List<OrderLine> lines() { return lines; }
    public int totalUnits() { return totalUnits; }
    public double createdAt() { return createdAt; }
    public boolean immediate() { return immediate; }
    public OrderStatus status() { return status; }
    public WaitReason waitReason() { return waitReason; }
    public double startedAt() { return startedAt; }
    public double completedAt() { return completedAt; }
    public double progress() { return progress; }
    public double remainingMinutes() { return remainingMinutes; }
    public ResourceType waitingFor() { return waitingFor; }
    public List<ResourceType> heldResources() { return List.copyOf(held); }
    public String deferredUntil() { return deferredUntil; }

    /* Minutes spent in the waiting list before the run that completed it. */
    public double waitedMinutes() { return startedAt - createdAt; }
    public double processingMinutes() { return completedAt - startedAt; }

    public void waitFor(WaitReason reason) {
        this.waitReason = reason;
    }

    public synchronized void admit(double nowMinutes) {
        status = OrderStatus.PROCESSING;
        startedAt = nowMinutes;
        progress = 0;
        remainingMinutes = workMinutes();
    }

    @Override
    public synchronized void blockedOn(ResourceType resource) {
        waitingFor = resource;
        status = OrderStatus.BLOCKED;
    }

    @Override
    public synchronized void acquired(ResourceType resource) {
        held.add(resource);
        waitingFor = null;
        status = OrderStatus.PROCESSING;
    }

    @Override
    public void progressed(double completedFraction, double remaining) {
        this.progress = completedFraction;
        this.remainingMinutes = remaining;
    }

    public synchronized void complete(double nowMinutes) {
        status = OrderStatus.COMPLETED;
        completedAt = nowMinutes;
        progress = 1;
        remainingMinutes = 0;
        held.clear();
    }

    /*
     * A conflict resolution took the order's resources away. It restarts from
     * scratch and stays out of the way of the winner (deferredUntil) until that
     * order is done.
     */
    public synchronized void returnToWaiting() {
        status = OrderStatus.WAITING;
        waitReason = WaitReason.RETURNED_BY_CONFLICT;
        startedAt = Double.NaN;
        progress = 0;
        remainingMinutes = workMinutes();
        waitingFor = null;
        held.clear();
    }

    public void deferUntil(String winnerOrderId) {
        this.deferredUntil = winnerOrderId;
    }
}
