package com.redxela.deadlock;

import com.redxela.domain.client.ClientRegistry;
import com.redxela.domain.exception.InvalidRequestException;
import com.redxela.domain.order.Order;
import com.redxela.domain.order.OrderRequest;
import com.redxela.domain.order.OrderRequest.LineRequest;
import com.redxela.scheduling.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;

/*
 * The realistic scenario of two orders that need the same two shared
 * resources, the barcode scanner and the loading areas, in opposite order: a
 * high-value order takes the scanner and then wants a loading area, while a
 * refrigerated order takes both loading areas and then wants the scanner.
 * Both are submitted as immediate so they start together without waiting for
 * resources to be reserved, which is what lets the circular wait form.
 */
@RequiredArgsConstructor
public class ConflictScenario {

    private static final String HIGH_VALUE_SKU = "VAL-001";
    private static final String REFRIGERATED_SKU = "REF-001";
    private static final int UNITS = 2;

    private final OrderService orders;
    private final ClientRegistry clients;

    public List<Order> provoke() {
        String clientId = clients.all().stream().findFirst()
                .orElseThrow(() -> new InvalidRequestException("There are no clients to place the orders for"))
                .id();
        return List.of(submit(clientId, HIGH_VALUE_SKU), submit(clientId, REFRIGERATED_SKU));
    }

    private Order submit(String clientId, String sku) {
        return orders.submit(new OrderRequest(clientId, List.of(new LineRequest(sku, UNITS)), true));
    }
}