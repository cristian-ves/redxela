package com.redxela.api.view;

import com.redxela.generator.OrderGenerator;
import com.redxela.inventory.AutoReplenisher;
import com.redxela.resources.AcquisitionOrder;
import com.redxela.scheduling.policy.PolicySelector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ControlsViewMapper {

    private final PolicySelector policy;
    private final OrderGenerator generator;
    private final AcquisitionOrder acquisitionOrder;
    private final AutoReplenisher replenisher;

    public ControlsView toView() {
        return new ControlsView(policy.current().name(), generator.isRunning(), generator.ordersPerMinute(),
                generator.seed(), acquisitionOrder.isGlobalOrdering(), replenisher.isEnabled());
    }
}