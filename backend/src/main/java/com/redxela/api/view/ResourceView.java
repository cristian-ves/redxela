package com.redxela.api.view;

import java.util.List;

/*
 * State of one resource pool: how many units exist, how many are free, who
 * holds them and who is waiting (with how many units each). This is what
 * explains why an operation is blocked.
 */
public record ResourceView(String type, String label, int capacity, int available,
                           List<Holding> holders, List<Holding> waiters) {

    public record Holding(String owner, int units) {
    }
}