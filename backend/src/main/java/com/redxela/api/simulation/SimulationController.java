package com.redxela.api.simulation;

import com.redxela.clock.SimulationClock;
import com.redxela.domain.exception.InvalidRequestException;
import com.redxela.inventory.AutoReplenisher;
import com.redxela.scheduling.policy.PolicySelector;
import com.redxela.scheduling.policy.PolicyType;
import com.redxela.stats.StatsCollector;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
 * General controls of the simulation: speed, scheduling policy, automatic
 * supplier and statistics. None of them returns data; the new state shows up
 * in the next snapshot.
 */
@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationClock clock;
    private final PolicySelector policy;
    private final AutoReplenisher replenisher;
    private final StatsCollector stats;

    @PutMapping("/speed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSpeed(@RequestBody SpeedRequest request) {
        clock.setSpeed(request.value());
    }

    @PutMapping("/policy")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void selectPolicy(@RequestBody PolicyRequest request) {
        policy.select(policyOf(request.policy()));
    }

    @PutMapping("/replenisher")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setReplenisher(@RequestBody FlagRequest request) {
        replenisher.setEnabled(request.enabled());
    }

    @PostMapping("/stats/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetStats() {
        stats.reset(clock.nowMinutes());
    }

    private PolicyType policyOf(String name) {
        return Arrays.stream(PolicyType.values())
                .filter(type -> type.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Unknown policy: " + name));
    }

    public record SpeedRequest(double value) {
    }

    public record PolicyRequest(String policy) {
    }

    public record FlagRequest(boolean enabled) {
    }
}