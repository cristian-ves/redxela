package com.redxela.warehouse;

/* What one storage slot currently holds. lotId, sku and units are empty for a free slot. */
public record SlotState(int index, String location, String lotId, String sku, int units) {

    public boolean isFree() {
        return lotId == null;
    }
}