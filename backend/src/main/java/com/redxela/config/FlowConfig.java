package com.redxela.config;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.inventory.AutoReplenisher;
import com.redxela.inventory.Inventory;
import com.redxela.inventory.InventoryWorker;
import com.redxela.messaging.ArrivalMessage;
import com.redxela.messaging.BoundedBuffer;
import com.redxela.reception.ReceptionArea;
import com.redxela.resources.OperationRunner;
import com.redxela.scheduling.DispatchSignal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* The Reception to Inventory link: the dock, the bounded buffer between the two areas, and the supplier. */
@Configuration(proxyBeanMethods = false)
public class FlowConfig {

    private static final int BUFFER_CAPACITY = 5;

    @Bean
    public BoundedBuffer<ArrivalMessage> arrivalBuffer() {
        return new BoundedBuffer<>(BUFFER_CAPACITY);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ReceptionArea receptionArea(ProductCatalog catalog, OperationRunner runner,
                                       BoundedBuffer<ArrivalMessage> arrivalBuffer) {
        return new ReceptionArea(catalog, runner, arrivalBuffer);
    }

    /* Wakes the dispatcher after storing a lot, so orders waiting for that stock are looked at again. */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public InventoryWorker inventoryWorker(BoundedBuffer<ArrivalMessage> arrivalBuffer, Inventory inventory,
                                           SimulationClock clock, DispatchSignal signal) {
        return new InventoryWorker(arrivalBuffer, inventory, clock, signal::request);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public AutoReplenisher autoReplenisher(Inventory inventory, ReceptionArea reception, SimulationClock clock) {
        return new AutoReplenisher(inventory, reception, clock);
    }
}