package com.redxela.reception;

/* Where a truck is in the reception area. */
public enum ReceptionStatus {
    QUEUED,
    RECEIVING,
    BLOCKED,
    WAITING_FOR_INVENTORY,
    SENT
}