package com.redxela.config;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.client.ClientRegistry;
import com.redxela.domain.order.OrderFactory;
import com.redxela.generator.OrderGenerator;
import com.redxela.generator.RandomOrderSource;
import com.redxela.inventory.Inventory;
import com.redxela.resources.OperationRunner;
import com.redxela.resources.ResourceManager;
import com.redxela.scheduling.AdmissionControl;
import com.redxela.scheduling.DispatchSignal;
import com.redxela.scheduling.Dispatcher;
import com.redxela.scheduling.OrderBook;
import com.redxela.scheduling.OrderExecutor;
import com.redxela.scheduling.OrderService;
import com.redxela.scheduling.WaitingList;
import com.redxela.scheduling.policy.PolicySelector;
import com.redxela.stats.StatsCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* Order intake, scheduling and the automatic generator. */
@Configuration(proxyBeanMethods = false)
public class SchedulingConfig {

    @Bean
    public OrderFactory orderFactory(ClientRegistry clients, ProductCatalog catalog) {
        return new OrderFactory(clients, catalog);
    }

    @Bean
    public OrderService orderService(OrderFactory factory, SimulationClock clock, OrderBook orderBook,
                                     WaitingList waitingList, DispatchSignal signal) {
        return new OrderService(factory, clock, orderBook, waitingList, signal);
    }

    @Bean
    public OrderExecutor orderExecutor(OperationRunner runner, Inventory inventory, OrderBook orderBook,
                                       WaitingList waitingList, StatsCollector stats, SimulationClock clock,
                                       DispatchSignal signal) {
        return new OrderExecutor(runner, inventory, orderBook, waitingList, stats, clock, signal::request);
    }

    @Bean
    public AdmissionControl admissionControl(ResourceManager resources, Inventory inventory, OrderBook orderBook) {
        return new AdmissionControl(resources, inventory, orderBook);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Dispatcher dispatcher(WaitingList waitingList, AdmissionControl admission, PolicySelector policy,
                                 OrderExecutor executor, SimulationClock clock, DispatchSignal signal) {
        return new Dispatcher(waitingList, admission, policy, executor, clock, signal);
    }

    @Bean
    public RandomOrderSource randomOrderSource(ClientRegistry clients, ProductCatalog catalog) {
        return new RandomOrderSource(clients, catalog);
    }

    /* Started on demand from the API, so only the destroy method is registered. */
    @Bean(destroyMethod = "stop")
    public OrderGenerator orderGenerator(RandomOrderSource source, OrderService orders, SimulationClock clock) {
        return new OrderGenerator(source, orders, clock);
    }
}