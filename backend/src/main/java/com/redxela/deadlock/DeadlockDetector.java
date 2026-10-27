package com.redxela.deadlock;

import com.redxela.domain.resource.ResourceType;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/*
 * Deadlock detection for resources with several identical units, using the
 * matrix algorithm: Allocation (what each operation holds), Request (what it
 * waits for) and Available (free units). Starting from the free units, any
 * operation whose request fits in what is available can finish and give its
 * units back, which may let others finish. The operations that can never
 * finish are deadlocked.
 *
 * A graph of single-unit resources would not be enough here: a pool such as
 * the warehouse staff has ten interchangeable units, and a request is
 * satisfiable as long as enough units are free, whoever holds the others.
 */
public class DeadlockDetector {

    public Set<String> detect(Collection<PoolState> pools) {
        Map<ResourceType, Integer> work = new EnumMap<>(ResourceType.class);
        Set<String> unfinished = new TreeSet<>();
        for (PoolState pool : pools) {
            work.put(pool.type(), pool.available());
            unfinished.addAll(pool.holders().keySet());
        }
        boolean progressed = true;
        while (progressed) {
            progressed = false;
            for (String operation : List.copyOf(unfinished)) {
                if (canFinish(operation, pools, work)) {
                    giveBackUnits(operation, pools, work);
                    unfinished.remove(operation);
                    progressed = true;
                }
            }
        }
        return unfinished;
    }

    private boolean canFinish(String operation, Collection<PoolState> pools, Map<ResourceType, Integer> work) {
        return pools.stream()
                .allMatch(pool -> pool.waiters().getOrDefault(operation, 0) <= work.get(pool.type()));
    }

    private void giveBackUnits(String operation, Collection<PoolState> pools, Map<ResourceType, Integer> work) {
        pools.forEach(pool -> work.merge(pool.type(), pool.holders().getOrDefault(operation, 0), Integer::sum));
    }
}