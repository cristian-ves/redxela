package com.redxela.domain.order;

import com.redxela.domain.catalog.MerchandiseType;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.client.Client;
import com.redxela.domain.client.ClientRegistry;
import com.redxela.domain.exception.InvalidRequestException;
import com.redxela.domain.order.OrderRequest.LineRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;

/*
 * Validates an order request and turns it into an Order. All the business
 * rules about what a valid order is live here, so the API layer and the
 * automatic generator build orders the same way.
 */
@RequiredArgsConstructor
public class OrderFactory {

    private static final int MAX_UNITS_PER_LINE = 100;

    private final ClientRegistry clients;
    private final ProductCatalog catalog;
    private final AtomicInteger sequence = new AtomicInteger();

    public Order create(OrderRequest request, double nowMinutes) {
        Client client = clients.find(request.clientId());
        List<OrderLine> lines = buildLines(request);
        MerchandiseType type = singleType(lines);
        String id = String.format("ORD-%04d", sequence.incrementAndGet());
        return new Order(id, client, type, lines, nowMinutes, request.immediate());
    }

    private List<OrderLine> buildLines(OrderRequest request) {
        if (request.lines() == null || request.lines().isEmpty()) {
            throw new InvalidRequestException("An order needs at least one product");
        }
        Map<String, Integer> unitsBySku = new LinkedHashMap<>();
        for (LineRequest line : request.lines()) {
            if (line.units() < 1 || line.units() > MAX_UNITS_PER_LINE) {
                throw new InvalidRequestException(
                        "Units per product must be between 1 and " + MAX_UNITS_PER_LINE);
            }
            unitsBySku.merge(line.sku(), line.units(), Integer::sum);
        }
        return unitsBySku.entrySet().stream()
                .map(entry -> new OrderLine(catalog.find(entry.getKey()), entry.getValue()))
                .toList();
    }

    private MerchandiseType singleType(List<OrderLine> lines) {
        MerchandiseType type = lines.get(0).product().type();
        boolean mixed = lines.stream().anyMatch(line -> line.product().type() != type);
        if (mixed) {
            throw new InvalidRequestException("All products of an order must share the same merchandise type");
        }
        return type;
    }
}
