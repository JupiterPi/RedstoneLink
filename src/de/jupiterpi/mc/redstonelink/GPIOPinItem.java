package de.jupiterpi.mc.redstonelink;

import de.jupiterpi.mc.redstonelink.pins.GPIOControlUnit;
import de.jupiterpi.mc.redstonelink.pins.GPIOPin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GPIOPinItem implements CommandExecutor {
    private GPIOControlUnit controlUnit = RedstoneLinkPlugin.controlUnit;

    public static String ITEM_NAME = ChatColor.WHITE + "GPIO %s Pin %s";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        int number = Integer.parseInt(args[0]);
        boolean isOut = args[1].equals("out");

        for (GPIOPin gpioPin : controlUnit.getPins()) {
            if (gpioPin.getNumber() == number) {
                player.sendMessage("This pin already exists. ");
                return true;
            }
        }

        player.getInventory().addItem(makeItem(number, isOut));

        return true;
    }

    private ItemStack makeItem(int number, boolean isOut) {
        ItemStack item = new ItemStack((isOut ? Material.REDSTONE_LAMP : Material.REDSTONE_BLOCK));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(String.format(ITEM_NAME, (isOut ? "Output" : "Input"), number));
        item.setItemMeta(meta);
        return item;
    }
}