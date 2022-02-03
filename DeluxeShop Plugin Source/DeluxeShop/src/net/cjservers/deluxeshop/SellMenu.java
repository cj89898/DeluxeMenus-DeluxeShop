package net.cjservers.deluxeshop;

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

public class SellMenu implements Listener {
  
  private String title;
  private Inventory inv;
  private LinkedHashMap<String, ShopItem> allItems;
  private List<ItemStack> menuItems;
  
  public SellMenu(LinkedHashMap<String, ShopItem> allItems, List<ItemStack> menuItems, String title) {
    // Put the items into the inventory
    this.allItems = allItems;
    this.menuItems = menuItems;
    this.title = ChatColor.translateAlternateColorCodes('&', title);
    initializeItems();
  }
  
  public void initializeItems() {
    inv = Bukkit.createInventory(null, 54, title);
    for (int i = 45; i <= 48; i++) {
      inv.setItem(i, menuItems.get(0));
    }
    inv.setItem(49, menuItems.get(1));
    for (int i = 50; i <= 53; i++) {
      inv.setItem(i, menuItems.get(2));
    }
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
    if (e.getRawSlot() >= 45 && e.getRawSlot() <= 53)
      e.setCancelled(true);
    if (e.getRawSlot() >= 45 && e.getRawSlot() <= 48) {
      p.getOpenInventory().close();
    } else if (e.getRawSlot() >= 50 && e.getRawSlot() <= 53) {
      double totalValue = 0;
      double totalAmount = 0;
      for (int i = 0; i <= 44; i++) {
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
  
  @EventHandler
  public void onInventoryClick(final InventoryDragEvent e) {
    if (e.getInventory() == inv) {
      for (int i = 45; i <= 53; i++) {
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
      for (int i = 0; i <= 44; i++) {
        if (inv.getItem(i) != null) {
          p.getInventory().addItem(inv.getItem(i));
          inv.setItem(i, null);
        }
      }
      for (ItemStack i : p.getInventory().getContents()) {
        if (i != null
            && (i.isSimilar(menuItems.get(0)) || i.isSimilar(menuItems.get(1)) || i.isSimilar(menuItems.get(2)))) {
          p.getInventory().remove(i);
        }
      }
      p.updateInventory();
    }
  }
  
  public void setMenuItems(List<ItemStack> sellMenuItems) {
    this.menuItems = sellMenuItems;
    initializeItems();
  }
  
  public void setTitle(String title) {
    this.title = ChatColor.translateAlternateColorCodes('&', title);
    initializeItems();
  }
}