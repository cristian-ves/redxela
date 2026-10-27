package com.redxela.api.reception;

import com.redxela.inventory.ProcessedArrival;
import com.redxela.messaging.ArrivalMessage;
import java.util.List;

/*
 * The whole Reception to Inventory link: trucks waiting at the gate, the one
 * being unloaded, the messages sitting in the bounded buffer, the trucks
 * already sent and what Inventory did with each message.
 */
public record ReceptionView(int queueCapacity, List<TruckView> queued, TruckView current,
                            int bufferCapacity, List<ArrivalMessage> buffer,
                            List<TruckView> recent, List<ProcessedArrival> processed) {

    public record TruckView(String id, String sku, String product, int units, String status,
                            String waitingFor, List<String> held, double progress, double remainingMinutes) {
    }
}