package com.redxela.messaging;

/*
 * What Reception tells Inventory: which product arrived and how many units.
 * It is the only thing the two areas share, so neither touches the other's
 * data structures.
 */
public record ArrivalMessage(String receptionId, String sku, int units) {
}