package com.redxela.domain.resource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * The shared resource pools of the distribution center and how many units each
 * one has. The ordering rank defines the global acquisition order used by
 * deadlock prevention: when it is enabled every operation requests its
 * resources by ascending rank, so a circular wait can never form.
 */
@Getter
@RequiredArgsConstructor
public enum ResourceType {
    PACKING_STATION("Packing stations", 6, 5),
    LOADING_AREA("Loading areas", 2, 2),
    WAREHOUSE_STAFF("Warehouse staff", 10, 6),
    FORKLIFT("Forklifts", 3, 3),
    QC_STATION("QC stations", 4, 4),
    SCANNER("Barcode scanner", 1, 1);

    private final String label;
    private final int capacity;
    private final int orderingRank;
}
