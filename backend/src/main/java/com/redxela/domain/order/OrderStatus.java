package com.redxela.domain.order;

/*
 * Life cycle of an order. BLOCKED is a processing order that already holds
 * some resources and is waiting for the next one (hold and wait).
 */
public enum OrderStatus {
    WAITING,
    PROCESSING,
    BLOCKED,
    COMPLETED
}
