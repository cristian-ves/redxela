package com.redxela.inventory;

/* Stock of one product: on hand, promised to admitted orders, and what is left to promise. */
public record StockLevel(String sku, String name, int onHand, int reserved) {

    public int available() {
        return onHand - reserved;
    }
}