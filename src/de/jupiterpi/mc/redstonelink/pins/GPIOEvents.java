package de.jupiterpi.mc.redstonelink.pins;

import de.jupiterpi.mc.redstonelink.GPIOPinItem;
import de.jupiterpi.mc.redstonelink.RedstoneLinkPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GPIOEvents implements Listener {
    private static GPIOControlUnit controlUnit = RedstoneLinkPlugin.controlUnit;

    @EventHandler
    public static void onRedstone(BlockRedstoneEvent event) {
        Block b = event.getBlock();
        if (b.getType() == Material.REDSTONE_LAMP) {
            for (GPIOPin gpioPin : controlUnit.getPins()) {
                if (gpioPin.getBlock().distance(b.getLocation()) <= 1.0) {
                    Bukkit.getScheduler().runTaskLater(RedstoneLinkPlugin.plugin, () -> {
                        Lightable data = (Lightable) b.getBlockData();
                        boolean lit = data.isLit();
                        controlUnit.output(gpioPin.getNumber(), lit);
                    }, 1);
                }
            }
        }
    }

    @EventHandler
    public static void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() == Material.REDSTONE_LAMP || item.getType() == Material.REDSTONE_BLOCK) {
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();
            if (name.startsWith(GPIOPinItem.ITEM_NAME.substring(0, 4))) {
                String[] parts = name.split(" ");
                String lastPart = parts[parts.length-1];
                int number = Integer.parseInt(lastPart);
                boolean isOut = parts[parts.length-3].equals("Output");
                Location location = event.getBlockPlaced().getLocation();
                for (GPIOPin gpioPin : controlUnit.getPins()) {
                    if (gpioPin.getNumber() == number) {
                        event.getPlayer().sendMessage("This pin already exists.");
                        return;
                    }
                }
                if (isOut) {
                    controlUnit.addOutputPin(number, location);
                } else {
                    controlUnit.addInputPin(number, location);
                }
                event.getPlayer().sendMessage("Added " + (isOut ? "output" : "input") + " pin " + number + ". ");
                event.getItemInHand().setAmount(0);
            }
        }
    }

    @EventHandler
    public static void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (b.getType() == Material.REDSTONE_LAMP || b.getType() == Material.REDSTONE_BLOCK || b.getType() == Material.STONE) {
            List<GPIOPin> toRemove = new ArrayList<>();
            for (GPIOPin gpioPin : controlUnit.getPins()) {
                if (gpioPin.getBlock().distance(b.getLocation()) < 1.0f) {
                    event.getPlayer().sendMessage("Removed " + (gpioPin instanceof GPIOOutputPin ? "output" : "input") + " pin " + gpioPin.getNumber() + ". ");
                    toRemove.add(gpioPin);
                }
            }
            for (GPIOPin gpioPin : toRemove) {
                controlUnit.removePin(gpioPin.getNumber());
            }
        }
    }
}