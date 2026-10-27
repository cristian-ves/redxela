package com.redxela.config;

import com.redxela.clock.SimulationClock;
import com.redxela.deadlock.ConflictResolver;
import com.redxela.deadlock.ConflictScenario;
import com.redxela.deadlock.DeadlockDetector;
import com.redxela.deadlock.DeadlockMonitor;
import com.redxela.deadlock.DeadlockReporter;
import com.redxela.domain.client.ClientRegistry;
import com.redxela.resources.ResourceManager;
import com.redxela.scheduling.OrderBook;
import com.redxela.scheduling.OrderExecutor;
import com.redxela.scheduling.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* Deadlock detection, the manual resolution and the scenario that provokes the conflict on purpose. */
@Configuration(proxyBeanMethods = false)
public class DeadlockConfig {

    @Bean
    public DeadlockDetector deadlockDetector() {
        return new DeadlockDetector();
    }

    /* Receptions are not in the order book, which is how the report tells them apart from orders. */
    @Bean
    public DeadlockReporter deadlockReporter(OrderBook orderBook) {
        return new DeadlockReporter(id -> orderBook.find(id) != null);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DeadlockMonitor deadlockMonitor(ResourceManager resources, DeadlockDetector detector,
                                           DeadlockReporter reporter, SimulationClock clock) {
        return new DeadlockMonitor(resources, detector, reporter, clock);
    }

    @Bean
    public ConflictResolver conflictResolver(DeadlockMonitor monitor, OrderBook orderBook, OrderExecutor executor) {
        return new ConflictResolver(monitor, orderBook, executor);
    }

    @Bean
    public ConflictScenario conflictScenario(OrderService orders, ClientRegistry clients) {
        return new ConflictScenario(orders, clients);
    }
}