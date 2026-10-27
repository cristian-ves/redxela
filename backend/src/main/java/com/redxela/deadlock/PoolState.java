package com.redxela.deadlock;

import com.redxela.domain.resource.ResourceType;
import com.redxela.resources.ResourcePool;
import java.util.Map;

/*
 * A frozen copy of one resource pool: how many units are free, who holds how
 * many and who is waiting for how many. The detector works on these copies
 * instead of the live pools, which keeps it free of threads and easy to test.
 */
public record PoolState(ResourceType type, int available, Map<String, Integer> holders, Map<String, Integer> waiters) {

    static PoolState of(ResourcePool pool) {
        return new PoolState(pool.type(), pool.available(), pool.holders(), pool.waiters());
    }
}