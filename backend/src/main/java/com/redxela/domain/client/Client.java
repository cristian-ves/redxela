package com.redxela.domain.client;

/* A client account. The service level is chosen when the account is created and every order inherits it. */
public record Client(String id, String name, ServiceLevel level) {
}
