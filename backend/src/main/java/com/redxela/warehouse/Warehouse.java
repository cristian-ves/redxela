package com.redxela.warehouse;

import com.redxela.domain.exception.CapacityExceededException;
import com.redxela.domain.exception.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/*
 * Physical storage of the warehouse, managed like paged memory: space is cut
 * into fixed-size slots (the frames), a lot is spread over as many slots as it
 * needs (its pages) and the lot-to-slots table plays the role of a page table.
 * Slots are handed out lowest index first from the free set, so after lots are
 * partly consumed and their slots freed, a new lot lands in scattered,
 * non-contiguous locations, and nothing is lost to external fragmentation.
 *
 * Every method is synchronized on the warehouse: slot contents, the free set
 * and the lot table must change together.
 */
public class Warehouse {

    public static final int SLOT_COUNT = 120;
    public static final int SLOT_CAPACITY_UNITS = 10;

    private final String[] slotLot = new String[SLOT_COUNT];
    private final String[] slotSku = new String[SLOT_COUNT];
    private final int[] slotUnits = new int[SLOT_COUNT];
    private final TreeSet<Integer> freeSlots = new TreeSet<>();
    private final Map<String, List<Integer>> slotsByLot = new LinkedHashMap<>();

    public Warehouse() {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            freeSlots.add(slot);
        }
    }

    /* Stores a lot over the first free slots and returns the slots it was given. */
    public synchronized List<Integer> store(String lotId, String sku, int units) {
        int needed = (units + SLOT_CAPACITY_UNITS - 1) / SLOT_CAPACITY_UNITS;
        if (needed > freeSlots.size()) {
            throw new CapacityExceededException("Warehouse is full: the lot needs " + needed
                    + " locations and only " + freeSlots.size() + " are free");
        }
        List<Integer> assigned = new ArrayList<>();
        int remaining = units;
        while (remaining > 0) {
            int slot = freeSlots.pollFirst();
            int placed = Math.min(remaining, SLOT_CAPACITY_UNITS);
            slotLot[slot] = lotId;
            slotSku[slot] = sku;
            slotUnits[slot] = placed;
            assigned.add(slot);
            remaining -= placed;
        }
        slotsByLot.put(lotId, assigned);
        return List.copyOf(assigned);
    }

    /* Takes units out of a lot, oldest slot first, and frees every slot that ends up empty. */
    public synchronized void withdraw(String lotId, int units) {
        List<Integer> slots = slotsByLot.get(lotId);
        if (slots == null) {
            throw new EntityNotFoundException("Unknown lot: " + lotId);
        }
        int remaining = units;
        for (Iterator<Integer> iterator = slots.iterator(); iterator.hasNext() && remaining > 0; ) {
            int slot = iterator.next();
            int taken = Math.min(remaining, slotUnits[slot]);
            slotUnits[slot] -= taken;
            remaining -= taken;
            if (slotUnits[slot] == 0) {
                clear(slot);
                iterator.remove();
            }
        }
        if (slots.isEmpty()) {
            slotsByLot.remove(lotId);
        }
    }

    public synchronized int unitsOf(String lotId) {
        List<Integer> slots = slotsByLot.get(lotId);
        return slots == null ? 0 : slots.stream().mapToInt(slot -> slotUnits[slot]).sum();
    }

    public synchronized List<Integer> slotsOf(String lotId) {
        List<Integer> slots = slotsByLot.get(lotId);
        return slots == null ? List.of() : List.copyOf(slots);
    }

    public synchronized int freeSlotCount() {
        return freeSlots.size();
    }

    public synchronized List<SlotState> snapshot() {
        List<SlotState> states = new ArrayList<>(SLOT_COUNT);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            states.add(new SlotState(slot, SlotLocation.fromIndex(slot).label(),
                    slotLot[slot], slotSku[slot], slotUnits[slot]));
        }
        return states;
    }

    private void clear(int slot) {
        slotLot[slot] = null;
        slotSku[slot] = null;
        slotUnits[slot] = 0;
        freeSlots.add(slot);
    }
}