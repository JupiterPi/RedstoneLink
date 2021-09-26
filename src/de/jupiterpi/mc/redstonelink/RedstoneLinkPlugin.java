package de.jupiterpi.mc.redstonelink;

import com.jcraft.jsch.JSchException;
import de.jupiterpi.mc.redstonelink.pins.GPIOControlUnit;
import de.jupiterpi.mc.redstonelink.pins.GPIOEvents;
import de.jupiterpi.mc.redstonelink.storage.PinsStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class RedstoneLinkPlugin extends JavaPlugin {
    public static Plugin plugin = null;
    public static GPIOControlUnit controlUnit;
    public static PinIndicators pinIndicators;
    public static PinsStorage storage;

    @Override
    public void onEnable() {
        plugin = this;

        try {
            controlUnit = new GPIOControlUnit();
        } catch (JSchException e) {
            e.printStackTrace();
        }

        Bukkit.getPluginManager().registerEvents(new GPIOEvents(), plugin);
        getCommand("pin").setExecutor(new GPIOPinItem());

        applyStatesTask();

        new Thread(() -> {
            pinIndicators = new PinIndicators();
        }).start();

        storage = new PinsStorage();
        storage.reload();

        getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[JuFo] Redstone Link Plugin enabled");
    }

    private void applyStatesTask() {
        controlUnit.applyAllStates();
        Bukkit.getScheduler().runTaskLater(plugin, this::applyStatesTask, 2);
    }

    @Override
    public void onDisable() {
        storage.save();
        controlUnit.removeAllPins();

        getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[JuFo] Redstone Link Plugin disabled");
    }
}