package com.redxela.domain.client;

import com.redxela.domain.exception.EntityNotFoundException;
import com.redxela.domain.exception.InvalidRequestException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * In-memory directory of client accounts. Registration is synchronized so two
 * concurrent requests cannot create the same name twice; reads are lock-free.
 */
public class ClientRegistry {

    private final Map<String, Client> clientsById = new ConcurrentHashMap<>();
    private int sequence;

    public synchronized Client register(String name, ServiceLevel level) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("Client name is required");
        }
        if (level == null) {
            throw new InvalidRequestException("Service level is required");
        }
        String cleanName = name.trim();
        boolean taken = clientsById.values().stream().anyMatch(client -> client.name().equalsIgnoreCase(cleanName));
        if (taken) {
            throw new InvalidRequestException("A client named '" + cleanName + "' already exists");
        }
        Client client = new Client(String.format("CLI-%03d", ++sequence), cleanName, level);
        clientsById.put(client.id(), client);
        return client;
    }

    public Client find(String id) {
        Client client = id == null ? null : clientsById.get(id);
        if (client == null) {
            throw new EntityNotFoundException("Unknown client: " + id);
        }
        return client;
    }

    public List<Client> all() {
        return clientsById.values().stream().sorted(Comparator.comparing(Client::id)).toList();
    }
}
