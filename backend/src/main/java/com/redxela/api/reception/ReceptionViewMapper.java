package com.redxela.api.reception;

import com.redxela.api.reception.ReceptionView.TruckView;
import com.redxela.inventory.InventoryWorker;
import com.redxela.messaging.ArrivalMessage;
import com.redxela.messaging.BoundedBuffer;
import com.redxela.reception.ReceptionArea;
import com.redxela.reception.ReceptionOperation;
import com.redxela.reception.ReceptionStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceptionViewMapper {

    private final ReceptionArea reception;
    private final BoundedBuffer<ArrivalMessage> buffer;
    private final InventoryWorker inventoryWorker;

    public ReceptionView toView() {
        ReceptionOperation current = reception.current();
        boolean unloading = current != null && current.status() != ReceptionStatus.SENT;
        return new ReceptionView(reception.queueCapacity(), trucks(reception.queued()),
                unloading ? toTruck(current) : null,
                buffer.capacity(), buffer.contents(),
                trucks(reception.recent()), inventoryWorker.recent());
    }

    public TruckView toTruck(ReceptionOperation truck) {
        return new TruckView(truck.id(), truck.product().sku(), truck.product().name(), truck.units(),
                truck.status().name(),
                truck.waitingFor() == null ? null : truck.waitingFor().name(),
                truck.heldResources().stream().map(Enum::name).toList(),
                truck.progress(), truck.remainingMinutes());
    }

    private List<TruckView> trucks(List<ReceptionOperation> operations) {
        return operations.stream().map(this::toTruck).toList();
    }
}