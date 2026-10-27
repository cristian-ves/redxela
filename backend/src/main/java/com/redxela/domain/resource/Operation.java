package com.redxela.domain.resource;

import java.util.List;

/*
 * Anything that runs on the shared resources: customer orders and truck
 * receptions. The resource layer drives the operation through these callbacks
 * and knows nothing else about it, which keeps it independent of the domain
 * objects that report the progress.
 */
public interface Operation {

    String id();

    List<ResourceRequirement> requirements();

    double workMinutes();

    /* The operation holds some resources and now has to wait for this one. */
    void blockedOn(ResourceType type);

    void acquired(ResourceType type);

    void progressed(double completedFraction, double remainingMinutes);
}
