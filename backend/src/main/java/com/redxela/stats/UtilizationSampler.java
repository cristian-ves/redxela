package com.redxela.stats;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.resource.ResourceType;
import com.redxela.resources.ResourceManager;
import com.redxela.resources.ResourcePool;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;

/*
 * Samples how busy every resource pool is a few times per second and feeds the
 * collector, which averages the samples per policy. Each sample is weighted by
 * the simulated time it stands for, so a change of speed does not bias the
 * average, and it says whether the system had open orders: samples taken while
 * everything is idle must not drag the averages down.
 */
@RequiredArgsConstructor
public class UtilizationSampler {

    private static final long SAMPLE_INTERVAL_MILLIS = 250;

    private final ResourceManager resources;
    private final SimulationClock clock;
    private final StatsCollector stats;
    private final BooleanSupplier hasOpenOrders;
    private Thread thread;

    public void start() {
        thread = new Thread(this::sampleForever, "utilization-sampler");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    private void sampleForever() {
        double lastMinutes = clock.nowMinutes();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(SAMPLE_INTERVAL_MILLIS);
                double now = clock.nowMinutes();
                stats.sampleUtilization(usedFractions(), now - lastMinutes, hasOpenOrders.getAsBoolean());
                lastMinutes = now;
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private Map<ResourceType, Double> usedFractions() {
        Map<ResourceType, Double> fractions = new EnumMap<>(ResourceType.class);
        for (ResourcePool pool : resources.pools()) {
            fractions.put(pool.type(), (pool.capacity() - pool.available()) / (double) pool.capacity());
        }
        return fractions;
    }
}
