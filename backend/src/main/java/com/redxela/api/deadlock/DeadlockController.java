package com.redxela.api.deadlock;

import com.redxela.deadlock.ConflictResolver;
import com.redxela.deadlock.ConflictScenario;
import com.redxela.domain.order.Order;
import com.redxela.resources.AcquisitionOrder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
 * Everything about deadlocks that is not just looking at the snapshot:
 * provoke the conflict on purpose, resolve it by choosing which order
 * continues, and switch on the prevention that makes it impossible.
 */
@RestController
@RequestMapping("/api/deadlock")
@RequiredArgsConstructor
public class DeadlockController {

    private final ConflictScenario scenario;
    private final ConflictResolver resolver;
    private final AcquisitionOrder acquisitionOrder;

    @PostMapping("/provoke")
    @ResponseStatus(HttpStatus.CREATED)
    public ProvokedOrders provoke() {
        return new ProvokedOrders(scenario.provoke().stream().map(Order::id).toList());
    }

    @PostMapping("/resolve")
    public ResolvedConflict resolve(@RequestBody ResolveRequest request) {
        return new ResolvedConflict(resolver.resolve(request.winnerId()));
    }

    @PutMapping("/prevention")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPrevention(@RequestBody PreventionRequest request) {
        acquisitionOrder.setGlobalOrdering(request.enabled());
    }

    public record ResolveRequest(String winnerId) {
    }

    public record PreventionRequest(boolean enabled) {
    }

    public record ProvokedOrders(List<String> orders) {
    }

    public record ResolvedConflict(List<String> returned) {
    }
}