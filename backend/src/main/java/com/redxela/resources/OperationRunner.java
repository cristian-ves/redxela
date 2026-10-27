package com.redxela.resources;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.resource.Operation;
import com.redxela.domain.resource.ResourceRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;

/*
 * Executes one operation on the shared resources: it requests the resources
 * one by one while keeping the ones it already holds, works while holding all
 * of them and gives everything back at the end. Holding while waiting is what
 * makes deadlock possible; the order in which the resources are requested is
 * decided by AcquisitionOrder.
 *
 * A short walk between two stations separates consecutive requests, which is
 * the window in which two operations can each take what the other needs next.
 */
@RequiredArgsConstructor
public class OperationRunner {

    private static final double WALK_MINUTES_BETWEEN_STATIONS = 1.0;
    private static final long PROGRESS_INTERVAL_MILLIS = 100;

    private final ResourceManager resources;
    private final AcquisitionOrder acquisitionOrder;
    private final SimulationClock clock;

    public void run(Operation operation) throws InterruptedException {
        List<ResourceRequirement> plan = acquisitionOrder.arrange(operation.requirements());
        try {
            acquireInOrder(operation, plan);
            work(operation);
        } finally {
            resources.releaseAll(operation.id());
        }
    }

    private void acquireInOrder(Operation operation, List<ResourceRequirement> plan) throws InterruptedException {
        for (int step = 0; step < plan.size(); step++) {
            if (step > 0) {
                clock.sleepMinutes(WALK_MINUTES_BETWEEN_STATIONS);
            }
            ResourceRequirement requirement = plan.get(step);
            resources.pool(requirement.type()).acquire(
                    operation.id(), requirement.units(), () -> operation.blockedOn(requirement.type()));
            operation.acquired(requirement.type());
        }
    }

    private void work(Operation operation) throws InterruptedException {
        double start = clock.nowMinutes();
        double end = start + operation.workMinutes();
        for (double now = start; now < end; now = clock.nowMinutes()) {
            operation.progressed((now - start) / (end - start), end - now);
            Thread.sleep(PROGRESS_INTERVAL_MILLIS);
        }
        operation.progressed(1.0, 0.0);
    }
}