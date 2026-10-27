package com.redxela.warehouse;

/* Physical position of a storage slot: aisle, shelf level and position along the shelf. */
public record SlotLocation(int aisle, int level, int position) {

    static final int LEVELS = 3;
    static final int POSITIONS = 5;

    public static SlotLocation fromIndex(int index) {
        int slotsPerAisle = LEVELS * POSITIONS;
        return new SlotLocation(
                index / slotsPerAisle + 1,
                (index % slotsPerAisle) / POSITIONS + 1,
                index % POSITIONS + 1);
    }

    public String label() {
        return "A" + aisle + "-L" + level + "-P" + position;
    }
}