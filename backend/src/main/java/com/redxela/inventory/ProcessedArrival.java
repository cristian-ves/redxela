package com.redxela.inventory;

import java.util.List;

/* Outcome of one message handled by Inventory: where the lot was put, or why it was turned away. */
public record ProcessedArrival(String receptionId, String sku, int units, String lotId,
                               List<String> locations, String outcome) {
}