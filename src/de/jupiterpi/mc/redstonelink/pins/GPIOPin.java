package de.jupiterpi.mc.redstonelink.pins;

import org.bukkit.Location;

public abstract class GPIOPin {
    protected int number;
    protected Location block;

    protected boolean cachedState = false;
    protected boolean state = false;

    // constructor

    protected GPIOPin(int number, Location block) {
        this.number = number;
        this.block = block;
    }

    // getters

    public int getNumber() {
        return number;
    }

    public Location getBlock() {
        return block;
    }

    // state manipulation

    public void cacheState(boolean state) {
        cachedState = state;
    }

    public abstract void applyState();
}