package com.redxela.config;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.catalog.ProductCatalog;
import com.redxela.domain.client.ClientRegistry;
import com.redxela.inventory.Inventory;
import com.redxela.resources.AcquisitionOrder;
import com.redxela.resources.OperationRunner;
import com.redxela.resources.ResourceManager;
import com.redxela.scheduling.DispatchSignal;
import com.redxela.scheduling.OrderBook;
import com.redxela.scheduling.WaitingList;
import com.redxela.scheduling.policy.PolicySelector;
import com.redxela.scheduling.policy.PolicyType;
import com.redxela.stats.StatsCollector;
import com.redxela.stats.UtilizationSampler;
import com.redxela.warehouse.Warehouse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Shared state of the simulation: clock, resources, stock and statistics.
 * The simulation classes are plain Java and know nothing about Spring; the
 * configuration classes of this package are the only place where they are
 * put together, and the container starts and stops their threads through the
 * init and destroy methods.
 */
@Configuration(proxyBeanMethods = false)
public class CoreConfig {

    @Bean
    public SimulationClock simulationClock() {
        return new SimulationClock();
    }

    @Bean
    public ClientRegistry clientRegistry() {
        return new ClientRegistry();
    }

    @Bean
    public ProductCatalog productCatalog() {
        return new ProductCatalog();
    }

    @Bean
    public ResourceManager resourceManager() {
        return new ResourceManager();
    }

    @Bean
    public AcquisitionOrder acquisitionOrder() {
        return new AcquisitionOrder();
    }

    @Bean
    public Warehouse warehouse() {
        return new Warehouse();
    }

    @Bean
    public Inventory inventory(Warehouse warehouse, ProductCatalog catalog) {
        return new Inventory(warehouse, catalog);
    }

    /*
     * The collector needs an active policy before the first sample or
     * completion arrives, otherwise those calls fail. It starts on the same
     * policy the selector uses by default.
     */
    @Bean
    public StatsCollector statsCollector() {
        StatsCollector stats = new StatsCollector();
        stats.switchPolicy(PolicyType.STRICT_PRIORITY.name());
        return stats;
    }

    @Bean
    public PolicySelector policySelector(StatsCollector stats) {
        return new PolicySelector(stats);
    }

    @Bean
    public DispatchSignal dispatchSignal() {
        return new DispatchSignal();
    }

    @Bean
    public OrderBook orderBook() {
        return new OrderBook();
    }

    @Bean
    public WaitingList waitingList() {
        return new WaitingList();
    }

    @Bean
    public OperationRunner operationRunner(ResourceManager resources, AcquisitionOrder order, SimulationClock clock) {
        return new OperationRunner(resources, order, clock);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public UtilizationSampler utilizationSampler(ResourceManager resources, SimulationClock clock, StatsCollector stats,
                                                 OrderBook orderBook) {
        return new UtilizationSampler(resources, clock, stats, orderBook::hasOpenOrders);
    }
}