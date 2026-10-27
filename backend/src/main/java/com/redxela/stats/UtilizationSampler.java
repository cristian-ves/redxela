package com.redxela.stats;

import com.redxela.domain.resource.ResourceType;
import com.redxela.resources.ResourceManager;
import com.redxela.resources.ResourcePool;
import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/*
 * Samples how busy every resource pool is a few times per second and feeds the
 * collector, which averages the samples per policy. Sampling at fixed real
 * intervals approximates the time-weighted occupancy without having to hook
 * into every acquire and release.
 */
@RequiredArgsConstructor
public class UtilizationSampler {

    private static final long SAMPLE_INTERVAL_MILLIS = 250;

    private final ResourceManager resources;
    private final StatsCollector stats;
    private Thread thread;

    public void start() {
        thread = new Thread(this::sampleForever, "utilization-sampler");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    private void sampleForever() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                stats.sampleUtilization(usedFractions());
                Thread.sleep(SAMPLE_INTERVAL_MILLIS);
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