package com.redxela.api.view;

import java.util.List;

/*
 * An order as the interface sees it. Enums travel as their names and the
 * human-readable text next to them; times are simulated minutes and progress
 * is a fraction between 0 and 1.
 */
public record OrderView(String id, String clientId, String clientName, int level, String levelLabel,
                        String type, String typeLabel, List<LineView> lines, int totalUnits, boolean immediate,
                        String status, String waitReason, String waitReasonLabel, String waitingFor,
                        List<String> held, double progress, double remainingMinutes, double createdAt,
                        double waitedMinutes, double processingMinutes, String deferredUntil) {

    public record LineView(String sku, String name, int units) {
    }
}