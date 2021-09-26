package de.jupiterpi.mc.redstonelink.pins;

import org.bukkit.Location;
import org.bukkit.Material;

public class GPIOInputPin extends GPIOPin {
    public GPIOInputPin(int number, Location block) {
        super(number, block);
    }

    @Override
    public void applyState() {
        if (cachedState != state) {
            Material material = cachedState ? Material.REDSTONE_BLOCK : Material.STONE;
            block.getBlock().setType(material);
            state = cachedState;
        }
    }
}
