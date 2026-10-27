package com.redxela.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* Why an order is sitting in the waiting list. */
@Getter
@RequiredArgsConstructor
public enum WaitReason {
    NO_RESOURCES("Waiting for resources"),
    NO_STOCK("Waiting for stock"),
    RETURNED_BY_CONFLICT("Returned by conflict resolution");

    private final String label;
}
