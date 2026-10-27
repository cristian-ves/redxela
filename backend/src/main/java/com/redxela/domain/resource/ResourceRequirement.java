package com.redxela.domain.resource;

/*
 * Units of one resource pool that an operation needs while it runs. A list of
 * requirements is ordered: operations request the resources one after the
 * other, in list order, keeping what they already hold.
 */
public record ResourceRequirement(ResourceType type, int units) {

    public ResourceRequirement {
        if (units < 1 || units > type.getCapacity()) {
            throw new IllegalArgumentException(
                    "Invalid unit count " + units + " for " + type + " (capacity " + type.getCapacity() + ")");
        }
    }

    public static ResourceRequirement of(ResourceType type, int units) {
        return new ResourceRequirement(type, units);
    }
}
