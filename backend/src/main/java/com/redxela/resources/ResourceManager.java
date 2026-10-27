package com.redxela.resources;

import com.redxela.domain.resource.ResourceRequirement;
import com.redxela.domain.resource.ResourceType;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/*
 * The six resource pools of the distribution center. The map is filled in the
 * constructor and never changed afterwards, so it is safe to read from any
 * thread without further synchronization.
 */
public class ResourceManager {

    private final Map<ResourceType, ResourcePool> pools = new EnumMap<>(ResourceType.class);

    public ResourceManager() {
        for (ResourceType type : ResourceType.values()) {
            pools.put(type, new ResourcePool(type));
        }
    }

    public ResourcePool pool(ResourceType type) {
        return pools.get(type);
    }

    public Collection<ResourcePool> pools() {
        return pools.values();
    }

    /*
     * Reserves every requirement or none. Only the dispatcher thread calls it,
     * so the check and the reservation cannot interleave with another reserver;
     * concurrent acquisitions only lower the availability, which keeps the
     * check conservative.
     */
    public boolean tryReserve(String owner, List<ResourceRequirement> requirements) {
        if (!canFit(requirements)) {
            return false;
        }
        requirements.forEach(requirement -> pool(requirement.type()).reserve(owner, requirement.units()));
        return true;
    }

    public boolean canFit(List<ResourceRequirement> requirements) {
        return requirements.stream()
                .allMatch(requirement -> pool(requirement.type()).unreserved() >= requirement.units());
    }

    public void releaseAll(String owner) {
        pools.values().forEach(pool -> pool.releaseAll(owner));
    }
}