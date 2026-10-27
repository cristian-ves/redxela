package com.redxela.generator;

import com.redxela.domain.catalog.MerchandiseType;
import com.redxela.domain.catalog.Product;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.client.Client;
import com.redxela.domain.client.ClientRegistry;
import com.redxela.domain.client.ServiceLevel;
import com.redxela.domain.order.OrderRequest;
import com.redxela.domain.order.OrderRequest.LineRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;

/*
 * Produces random but reproducible customer orders. The same seed gives the
 * same sequence of clients, products and quantities, so every scheduling
 * policy can be measured against an identical stream of arrivals. The gaps
 * between arrivals follow an exponential distribution, the usual model of
 * independent clients arriving at a constant average rate.
 */
@RequiredArgsConstructor
public class RandomOrderSource {

    private static final long DEFAULT_SEED = 42;
    private static final int MAX_LINES = 3;
    private static final int MAX_UNITS_PER_LINE = 8;
    private static final int[] LEVEL_WEIGHTS = {10, 15, 30, 25, 20};
    private static final int[] TYPE_WEIGHTS = {40, 25, 15, 12, 8};

    private final ClientRegistry clients;
    private final ProductCatalog catalog;
    private Random random = new Random(DEFAULT_SEED);

    public synchronized void reseed(Long seed) {
        random = new Random(seed == null ? DEFAULT_SEED : seed);
    }

    public synchronized OrderRequest nextRequest() {
        Client client = pickClient();
        List<Product> products = new ArrayList<>(catalog.ofType(pickType()));
        Collections.shuffle(products, random);
        int lines = 1 + random.nextInt(Math.min(MAX_LINES, products.size()));
        List<LineRequest> requested = products.subList(0, lines).stream()
                .map(product -> new LineRequest(product.sku(), 1 + random.nextInt(MAX_UNITS_PER_LINE)))
                .toList();
        return new OrderRequest(client.id(), requested, false);
    }

    /* Minutes until the next arrival for a given average rate of orders per minute. */
    public synchronized double nextGapMinutes(double ordersPerMinute) {
        return -Math.log(1 - random.nextDouble()) / ordersPerMinute;
    }

    private Client pickClient() {
        ServiceLevel level = ServiceLevel.values()[weightedIndex(LEVEL_WEIGHTS)];
        List<Client> all = clients.all();
        List<Client> sameLevel = all.stream().filter(client -> client.level() == level).toList();
        List<Client> candidates = sameLevel.isEmpty() ? all : sameLevel;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private MerchandiseType pickType() {
        return MerchandiseType.values()[weightedIndex(TYPE_WEIGHTS)];
    }

    private int weightedIndex(int[] weights) {
        int total = 0;
        for (int weight : weights) {
            total += weight;
        }
        int draw = random.nextInt(total);
        int index = 0;
        while (draw >= weights[index]) {
            draw -= weights[index];
            index++;
        }
        return index;
    }
}