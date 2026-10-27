package com.redxela.domain.order;

import com.redxela.domain.catalog.Product;

/* One product of an order and the number of units requested. */
public record OrderLine(Product product, int units) {
}
