package com.redxela.resources;

import com.redxela.domain.resource.ResourceRequirement;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Deadlock prevention switch. Off, every operation requests its resources in
 * the order its merchandise type defines, so two types that use the same pair
 * of resources in opposite order can end up waiting for each other. On, all
 * requests follow one global order (the ordering rank of each resource), which
 * removes the circular-wait condition.
 */
public class AcquisitionOrder {

    private final AtomicBoolean globalOrdering = new AtomicBoolean(false);

    public List<ResourceRequirement> arrange(List<ResourceRequirement> requirements) {
        if (!globalOrdering.get()) {
            return requirements;
        }
        return requirements.stream()
                .sorted(Comparator.comparingInt(requirement -> requirement.type().getOrderingRank()))
                .toList();
    }

    public void setGlobalOrdering(boolean enabled) {
        globalOrdering.set(enabled);
    }

    public boolean isGlobalOrdering() {
        return globalOrdering.get();
    }
}