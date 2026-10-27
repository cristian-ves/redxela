package com.redxela.inventory;

import java.util.List;

/* A lot that was put away: the slots it occupies, which need not be contiguous. */
public record StoredLot(String lotId, String sku, int units, List<Integer> slots) {
}