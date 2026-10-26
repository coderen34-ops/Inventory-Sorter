package com.sorter.inventorysorter;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Açık container'ı (sandık/varil/shulker/ender chest) sıralayan dinleyici.
 *
 * BUG DÜZELTMESİ (v1.3): Eskiden sadece InventoryType kontrolü yapılıyordu. Ama
 * Bukkit.createInventory(customHolder, 54, title) ile açılan ÖZEL plugin GUI'leri
 * (klan kasası, klan menüsü, diplomasi paneli, zindan menüleri vb.) da varsayılan
 * olarak InventoryType.CHEST döndürür - yani tip kontrolü onları normal bir sandıktan
 * ayırt edemiyordu. Bu da sıralamanın o GUI'lerin buton/cam (filler) slotlarını da
 * normal eşya gibi karıştırmasına, bazen cam'ın oyuncunun eline geçmesine ya da GUI
 * düzeninin bozulmasına yol açıyordu. Düzeltme: holder'ın GERÇEKTEN vanilla bir blok
 * konteynerine ait olup olmadığı da kontrol edilir - özel plugin holder'ları her zaman
 * kendi paketlerinde (org.bukkit dışında) olur, vanilla konteynerler (CraftChest vb.)
 * ise org.bukkit(.craftbukkit...) paketinde.
 */
public class SortListener implements Listener {

    public static final Set<InventoryType> SORTABLE_TYPES = EnumSet.of(
            InventoryType.CHEST, InventoryType.BARREL, InventoryType.SHULKER_BOX, InventoryType.ENDER_CHEST);

    public static boolean isSortable(Inventory inv) {
        if (inv == null || !SORTABLE_TYPES.contains(inv.getType())) return false;
        InventoryHolder holder = inv.getHolder();
        if (holder == null) return true;
        // Özel plugin GUI'leri hariç tut: sadece vanilla/CraftBukkit konteyner holder'ları geçer.
        return holder.getClass().getName().startsWith("org.bukkit.");
    }

    public static void sortInventory(Inventory inv) {
        List<ItemStack> esyalar = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) esyalar.add(item);
        }
        esyalar.sort(Comparator
                .comparing((ItemStack i) -> i.getType().name())
                .thenComparing(SortListener::displayName)
                .thenComparing(Comparator.comparingInt(ItemStack::getAmount).reversed()));
        inv.clear();
        for (int i = 0; i < esyalar.size(); i++) inv.setItem(i, esyalar.get(i));
    }

    private static String displayName(ItemStack item) {
        return item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Inventory ust = event.getView().getTopInventory();
        if (!isSortable(ust)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        boolean disaridaTiklama = event.getRawSlot() == -999
                && event.getCursor() != null && event.getCursor().getType() == Material.AIR;
        boolean ustSlotTetikleyici = event.getRawSlot() >= 0 && event.getRawSlot() < ust.getSize()
                && (event.getClick() == ClickType.MIDDLE || event.getClick() == ClickType.SWAP_OFFHAND);

        if (!disaridaTiklama && !ustSlotTetikleyici) return;

        event.setCancelled(true);
        sortInventory(ust);
        player.updateInventory();
        // Klan kasasındaki sıralamayla aynı ses
        player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_BUNDLE_INSERT, 0.5f, 1.2f);
    }
}
