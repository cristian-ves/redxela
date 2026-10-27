package com.redxela.api.simulation;

/* Every switch and setting the user can change while the simulation runs. */
public record ControlsView(String policy, boolean generatorRunning, double ordersPerMinute, Long seed,
                           boolean preventionEnabled, boolean replenisherEnabled) {
}