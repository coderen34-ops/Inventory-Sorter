package com.sorter.inventorysorter;

import org.bukkit.plugin.java.JavaPlugin;

public class InventorySorterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SortListener(), this);
        getCommand("sort").setExecutor(new SortCommand());
        getLogger().info("InventorySorter aktif edildi.");
    }

    @Override
    public void onDisable() {
        getLogger().info("InventorySorter devre dışı bırakıldı.");
    }
}
