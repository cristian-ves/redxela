package com.redxela.api.client;

import com.redxela.domain.client.Client;

/* A client account with its service level as a number (1 Express to 5 Economy) and its label. */
public record ClientView(String id, String name, int level, String levelLabel) {

    public static ClientView of(Client client) {
        return new ClientView(client.id(), client.name(), client.level().getLevel(), client.level().getLabel());
    }
}