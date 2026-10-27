package com.redxela.deadlock;

import com.redxela.domain.resource.ResourceType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

/*
 * Turns the set of deadlocked operation ids into a report a person can read:
 * for each operation what it holds, what it waits for and who holds that, and
 * a closing line that names the four conditions that make the stuck state
 * permanent.
 */
@RequiredArgsConstructor
public class DeadlockReporter {

    private static final String CONDITIONS = "All four deadlock conditions hold: mutual exclusion (a unit serves one "
            + "operation at a time), hold and wait (each keeps what it has while it waits), no preemption (units are "
            + "only given back by their holder) and circular wait (each one waits for another in the group).";

    private final Predicate<String> isOrder;

    public DeadlockReport build(Set<String> ids, Collection<PoolState> pools, double nowMinutes) {
        List<InvolvedOperation> operations = ids.stream().map(id -> describe(id, ids, pools)).toList();
        List<String> explanation = new ArrayList<>(operations.stream().map(this::sentence).toList());
        explanation.add(CONDITIONS);
        return new DeadlockReport(nowMinutes, operations, explanation);
    }

    private InvolvedOperation describe(String id, Set<String> ids, Collection<PoolState> pools) {
        Map<ResourceType, Integer> holds = new EnumMap<>(ResourceType.class);
        pools.stream().filter(pool -> pool.holders().containsKey(id))
                .forEach(pool -> holds.put(pool.type(), pool.holders().get(id)));
        PoolState awaited = pools.stream().filter(pool -> pool.waiters().containsKey(id)).findFirst().orElse(null);
        if (awaited == null) {
            return new InvolvedOperation(id, isOrder.test(id), holds, null, 0, List.of());
        }
        List<String> blockers = awaited.holders().keySet().stream()
                .filter(ids::contains).filter(other -> !other.equals(id)).sorted().toList();
        return new InvolvedOperation(id, isOrder.test(id), holds, awaited.type(), awaited.waiters().get(id), blockers);
    }

    private String sentence(InvolvedOperation operation) {
        String holds = operation.holds().entrySet().stream()
                .map(entry -> entry.getKey().getLabel() + " x" + entry.getValue())
                .collect(Collectors.joining(", "));
        if (operation.waitingFor() == null) {
            return operation.id() + " holds " + holds + " and is stuck behind the group.";
        }
        return operation.id() + " holds " + holds + " and waits for " + operation.waitingFor().getLabel()
                + " x" + operation.waitingForUnits() + ", held by " + String.join(", ", operation.waitingOn()) + ".";
    }
}