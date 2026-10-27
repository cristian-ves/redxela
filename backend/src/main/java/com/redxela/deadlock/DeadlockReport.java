package com.redxela.deadlock;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/* A confirmed deadlock, with a plain-language explanation for the interface. */
public record DeadlockReport(double detectedAtMinutes, List<InvolvedOperation> operations, List<String> explanation) {

    public Set<String> ids() {
        return operations.stream().map(InvolvedOperation::id).collect(Collectors.toSet());
    }

    public Optional<InvolvedOperation> find(String id) {
        return operations.stream().filter(operation -> operation.id().equals(id)).findFirst();
    }
}