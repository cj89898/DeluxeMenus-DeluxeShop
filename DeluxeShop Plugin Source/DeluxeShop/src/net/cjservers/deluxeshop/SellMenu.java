package net.cjservers.deluxeshop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SellMenu implements Listener {
  
  private final Inventory inv;
  private LinkedHashMap<String, ShopItem> allItems;
  
  public SellMenu(LinkedHashMap<String, ShopItem> allItems) {
    
    inv = Bukkit.createInventory(null, 36, "Example");
    // Put the items into the inventory
    this.allItems = allItems;
    initializeItems();
  }
  
  // You can call this whenever you want to put the items in
  public void initializeItems() {
    ItemStack cancel = createGuiItem(Material.DIAMOND_SWORD, "§cCancel", "§8Exit the menu");
    ItemStack sell = createGuiItem(Material.DIAMOND_SWORD, "§aSell Items", "§8Make money");
    for (int i = 27; i <= 30; i++) {
      inv.setItem(i, cancel);
    }
    inv.setItem(31, createGuiItem(Material.DIAMOND_SWORD, " "));
    for (int i = 32; i <= 35; i++) {
      inv.setItem(i, sell);
    }
  }
  
  // Nice little method to create a gui item with a custom name, and description
  protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();
    
    // Set the name of the item
    meta.setDisplayName(name);
    
    // Set the lore of the item
    meta.setLore(Arrays.asList(lore));
    
    item.setItemMeta(meta);
    
    return item;
  }
  
  // You can open the inventory with this
  public void openInventory(final HumanEntity ent) {
    ent.openInventory(inv);
  }
  
  // Check for clicks on items
  @EventHandler
  public void onInventoryClick(final InventoryClickEvent e) {
    
    if (e.getWhoClicked().getOpenInventory().getTopInventory() != inv && e.getClickedInventory() != inv)
      return;
    
    final Player p = (Player) e.getWhoClicked();
    if (e.getRawSlot() >= 27 && e.getRawSlot() <= 35)
      e.setCancelled(true);
    if (e.getRawSlot() >= 27 && e.getRawSlot() <= 30) {
      p.getOpenInventory().close();
    } else if (e.getRawSlot() >= 32 && e.getRawSlot() <= 35) {
      double totalValue = 0;
      double totalAmount = 0;
      for (int i = 0; i <= 27; i++) {
        if (inv.getItem(i) == null) {
          continue;
        }
        String itemName = inv.getItem(i).getType().toString().toLowerCase();
        if (!allItems.containsKey(itemName)) {
          continue;
        }
        double sell = allItems.get(itemName).getSell(p);
        ItemStack blankItem = new ItemStack(Material.getMaterial(itemName.toUpperCase()));
        if (inv.getItem(i).isSimilar(blankItem)) {
          int amount = inv.getItem(i).getAmount();
          inv.setItem(i, null);
          DeluxeShop.getEconomy().depositPlayer(p, sell * amount);
          totalAmount += amount;
          totalValue += (sell * amount);
        }
      }
      if (totalAmount == 0) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Nothing to sell!"));
      } else {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
            "&6Successfully sold &a" + totalAmount + " items &6for &a" + DeluxeShop.DECIMAL_FORMAT.format(totalValue)));
      }
      p.getOpenInventory().close();
    }
  }
  
  // Cancel dragging in our inventory
  @EventHandler
  public void onInventoryClick(final InventoryDragEvent e) {
    if (e.getInventory() == inv) {
      for (int i = 27; i <= 35; i++) {
        if (e.getRawSlots().contains(i)) {
          e.setCancelled(true);
        }
      }
    }
  }
  
  @EventHandler
  public void onInventoryClose(final InventoryCloseEvent e) {
    if (e.getInventory() == inv && e.getPlayer() instanceof Player) {
      Player p = (Player) e.getPlayer();
      for (int i = 0; i <= 26; i++) {
        if (inv.getItem(i) != null) {
          p.getInventory().addItem(inv.getItem(i));
          inv.setItem(i, null);
        }
      }
      List<String> itemNames = new ArrayList<String>();
      itemNames.add("§cCancel");
      itemNames.add("§aSell Items");
      for (ItemStack i : p.getInventory().getContents()) {
        if (i != null) {
          Bukkit.getLogger().info(i.getItemMeta().getDisplayName());
          if (i.getType() == Material.DIAMOND_SWORD
              && i.hasItemMeta()
              && i.getItemMeta().hasDisplayName()
              && itemNames.contains(i.getItemMeta().getDisplayName())) {
            p.getInventory().remove(i);
          }
        }
      }
      p.updateInventory();
    }
  }
}