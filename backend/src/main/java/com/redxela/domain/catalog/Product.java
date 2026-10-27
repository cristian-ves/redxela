package com.redxela.domain.catalog;

/* A catalog item. Its merchandise type fixes the resources any order containing it needs. */
public record Product(String sku, String name, MerchandiseType type) {
}
