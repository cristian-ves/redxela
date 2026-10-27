package com.redxela.api.view;

/* Stock of one product: units in the warehouse, units promised to admitted orders, and what is left to promise. */
public record StockView(String sku, String name, int onHand, int reserved, int available) {
}