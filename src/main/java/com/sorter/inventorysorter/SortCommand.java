package com.sorter.inventorysorter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SortCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut sadece oyun içinden kullanılabilir.");
            return true;
        }
        Inventory ust = player.getOpenInventory().getTopInventory();
        if (!SortListener.isSortable(ust)) {
            player.sendMessage(ChatColor.RED + "Önce bir sandık, varil, shulker ya da ender chest aç.");
            return true;
        }
        SortListener.sortInventory(ust);
        player.updateInventory();
        player.sendMessage(ChatColor.GREEN + "Sıralandı.");
        return true;
    }
}
