package com.redxela.api.snapshot;

/* Running totals since the server started. */
public record CountersView(long submitted, long completed, int waiting, int inProcess) {
}