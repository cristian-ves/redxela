package com.redxela.reception;

import com.redxela.domain.catalog.Product;
import com.redxela.domain.resource.Operation;
import com.redxela.domain.resource.ResourceRequirement;
import com.redxela.domain.resource.ResourceType;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.redxela.domain.resource.ResourceRequirement.of;

/*
 * One truck being unloaded. It is an operation like an order: a forklift
 * unloads it and then the shared scanner registers the load, so receptions
 * compete with order dispatch for the scanner.
 */
public class ReceptionOperation implements Operation {

    private static final List<ResourceRequirement> REQUIREMENTS =
            List.of(of(ResourceType.FORKLIFT, 1), of(ResourceType.SCANNER, 1));
    private static final double WORK_MINUTES_PER_UNIT = 0.05;
    private static final double MIN_WORK_MINUTES = 0.5;

    private final String id;
    private final Product product;
    private final int units;
    private final List<ResourceType> held = new CopyOnWriteArrayList<>();

    private volatile ReceptionStatus status = ReceptionStatus.QUEUED;
    private volatile ResourceType waitingFor;
    private volatile double progress;
    private volatile double remainingMinutes;

    public ReceptionOperation(String id, Product product, int units) {
        this.id = id;
        this.product = product;
        this.units = units;
        this.remainingMinutes = Math.max(MIN_WORK_MINUTES, units * WORK_MINUTES_PER_UNIT);
    }

    @Override public String id() { return id; }
    @Override public List<ResourceRequirement> requirements() { return REQUIREMENTS; }
    @Override public double workMinutes() { return Math.max(MIN_WORK_MINUTES, units * WORK_MINUTES_PER_UNIT); }

    public Product product() { return product; }
    public int units() { return units; }
    public ReceptionStatus status() { return status; }
    public ResourceType waitingFor() { return waitingFor; }
    public double progress() { return progress; }
    public double remainingMinutes() { return remainingMinutes; }
    public List<ResourceType> heldResources() { return List.copyOf(held); }

    public void startReceiving() { status = ReceptionStatus.RECEIVING; }
    public void waitForInventory() { status = ReceptionStatus.WAITING_FOR_INVENTORY; held.clear(); }
    public void markSent() { status = ReceptionStatus.SENT; }

    @Override
    public synchronized void blockedOn(ResourceType resource) {
        waitingFor = resource;
        status = ReceptionStatus.BLOCKED;
    }

    @Override
    public synchronized void acquired(ResourceType resource) {
        held.add(resource);
        waitingFor = null;
        status = ReceptionStatus.RECEIVING;
    }

    @Override
    public void progressed(double completedFraction, double remaining) {
        this.progress = completedFraction;
        this.remainingMinutes = remaining;
    }
}