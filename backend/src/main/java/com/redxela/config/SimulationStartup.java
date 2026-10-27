package com.redxela.config;

import com.redxela.domain.catalog.Product;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.client.ClientRegistry;
import com.redxela.domain.client.ServiceLevel;
import com.redxela.generator.OrderGenerator;
import com.redxela.inventory.Inventory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/*
 * Initial data, loaded once the application is up: one client per service
 * level and a starting stock of every product, so the interface has something
 * to work with from the first second. The generator only starts by itself if
 * redxela.generator.autostart is true.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationStartup implements ApplicationRunner {

    private static final int INITIAL_UNITS_PER_PRODUCT = 40;
    private static final List<String> CLIENT_NAMES = List.of(
            "Atlas Pharma", "Borealis Electronics", "Cobalt Hardware", "Delta Home Goods", "Ember Outlet");

    private final ClientRegistry clients;
    private final ProductCatalog catalog;
    private final Inventory inventory;
    private final OrderGenerator generator;

    @Value("${redxela.generator.autostart:false}")
    private boolean autostart;

    @Override
    public void run(ApplicationArguments args) {
        seedClients();
        seedStock();
        if (autostart) {
            generator.start(null);
        }
        log.info("Simulation ready: {} clients, {} products in stock", clients.all().size(), catalog.all().size());
    }

    private void seedClients() {
        ServiceLevel[] levels = ServiceLevel.values();
        for (int i = 0; i < levels.length; i++) {
            clients.register(CLIENT_NAMES.get(i), levels[i]);
        }
    }

    private void seedStock() {
        for (Product product : catalog.all()) {
            inventory.receive(product.sku(), INITIAL_UNITS_PER_PRODUCT);
        }
    }
}