package com.redxela.deadlock;

import com.redxela.domain.resource.ResourceType;
import java.util.List;
import java.util.Map;

/*
 * One operation caught in a deadlock: what it holds, what it is waiting for
 * and which of the other stuck operations hold that resource. "order" is
 * false for receptions, which cannot be chosen as winner or loser.
 */
public record InvolvedOperation(String id, boolean order, Map<ResourceType, Integer> holds,
                                ResourceType waitingFor, int waitingForUnits, List<String> waitingOn) {
}