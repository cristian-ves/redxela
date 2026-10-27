package com.redxela.api.client;

import com.redxela.domain.client.ClientRegistry;
import com.redxela.domain.client.ServiceLevel;
import com.redxela.domain.exception.InvalidRequestException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/* Client accounts. The service level is chosen once, when the account is created. */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRegistry clients;

    @GetMapping
    public List<ClientView> list() {
        return clients.all().stream().map(ClientView::of).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientView create(@RequestBody NewClient request) {
        return ClientView.of(clients.register(request.name(), levelOf(request.level())));
    }

    private ServiceLevel levelOf(int number) {
        return Arrays.stream(ServiceLevel.values())
                .filter(level -> level.getLevel() == number)
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("The service level must be a number from 1 to 5"));
    }

    public record NewClient(String name, int level) {
    }
}