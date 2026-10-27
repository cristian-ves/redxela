package com.redxela.domain.catalog;

import com.redxela.domain.resource.ResourceRequirement;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.redxela.domain.resource.ResourceRequirement.of;
import static com.redxela.domain.resource.ResourceType.FORKLIFT;
import static com.redxela.domain.resource.ResourceType.LOADING_AREA;
import static com.redxela.domain.resource.ResourceType.PACKING_STATION;
import static com.redxela.domain.resource.ResourceType.QC_STATION;
import static com.redxela.domain.resource.ResourceType.SCANNER;
import static com.redxela.domain.resource.ResourceType.WAREHOUSE_STAFF;

/*
 * Merchandise classes. The type, not the service level, decides which
 * resources an order needs, and every type needs a different combination.
 *
 * HIGH_VALUE and REFRIGERATED are the contention scenario of the project: both
 * need the scanner and a loading area, but in opposite order. High-value goods
 * are scanned before they are released to a loading area (chain of custody);
 * a reefer trailer takes both loading areas first and its manifest is scanned
 * once it is docked. Running together they hold what the other one waits for.
 */
@Getter
@RequiredArgsConstructor
public enum MerchandiseType {
    GENERAL("General",
            "Everyday goods: one packer and a packing station.",
            List.of(of(WAREHOUSE_STAFF, 1), of(PACKING_STATION, 1))),
    FRAGILE("Fragile",
            "Breakable goods: inspected at a QC station before they are packed.",
            List.of(of(WAREHOUSE_STAFF, 1), of(QC_STATION, 1), of(PACKING_STATION, 1))),
    HEAVY("Heavy",
            "Palletised goods: a forklift moves them, two staff members handle them and a loading area ships them.",
            List.of(of(FORKLIFT, 1), of(WAREHOUSE_STAFF, 2), of(LOADING_AREA, 1))),
    HIGH_VALUE("High value",
            "Chain-of-custody goods: scanned first, then released to a loading area.",
            List.of(of(WAREHOUSE_STAFF, 1), of(SCANNER, 1), of(LOADING_AREA, 1))),
    REFRIGERATED("Refrigerated",
            "Cold-chain goods: the reefer trailer takes both loading areas and is scanned once docked.",
            List.of(of(FORKLIFT, 1), of(LOADING_AREA, 2), of(SCANNER, 1)));

    private final String label;
    private final String description;
    private final List<ResourceRequirement> requirements;
}
