package de.jupiterpi.mc.redstonelink.pins;

import org.bukkit.Location;

import java.util.function.BiConsumer;

public class GPIOOutputPin extends GPIOPin {
    private BiConsumer<Integer, Boolean> outputPin;

    public GPIOOutputPin(int number, Location block, BiConsumer<Integer, Boolean> outputPin) {
        super(number, block);
        this.outputPin = outputPin;
    }

    @Override
    public void applyState() {
        if (cachedState != state) {
            outputPin.accept(number, cachedState);
            state = cachedState;
        }
    }
}