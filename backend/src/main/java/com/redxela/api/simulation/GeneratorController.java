package com.redxela.api.simulation;

import com.redxela.generator.OrderGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/* Automatic order generator: start it (optionally with a seed and a rate), stop it, or change the rate. */
@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final OrderGenerator generator;

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void start(@RequestBody(required = false) StartRequest request) {
        if (request != null && request.ordersPerMinute() != null) {
            generator.setOrdersPerMinute(request.ordersPerMinute());
        }
        generator.start(request == null ? null : request.seed());
    }

    @PostMapping("/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stop() {
        generator.stop();
    }

    @PutMapping("/rate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setRate(@RequestBody RateRequest request) {
        generator.setOrdersPerMinute(request.value());
    }

    public record StartRequest(Long seed, Double ordersPerMinute) {
    }

    public record RateRequest(double value) {
    }
}