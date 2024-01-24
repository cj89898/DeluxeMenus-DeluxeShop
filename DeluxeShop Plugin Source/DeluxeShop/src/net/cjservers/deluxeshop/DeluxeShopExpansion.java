package net.cjservers.deluxeshop;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class DeluxeShopExpansion extends PlaceholderExpansion {
  
  private final DeluxeShop plugin;
  
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
  public static final DecimalFormat POS_NEG = new DecimalFormat("+#;-#");
  
  public DeluxeShopExpansion(DeluxeShop plugin) {
    this.plugin = plugin;
  }
  
  @Override
  public String getAuthor() {
    return "cj89898";
  }
  
  @Override
  public String getIdentifier() {
    return "deluxeshop";
  }
  
  @Override
  public String getVersion() {
    return plugin.getDescription().getVersion();
  }
  
  @Override
  public boolean canRegister() {
    return true;
  }
  
  @Override
  public boolean persist() {
    return true;
  }
  
  @Override
  public String onRequest(OfflinePlayer player, String argsString) {
    if (plugin.getConfig().getBoolean("debug"))
      Bukkit.getLogger().info("[DeluxeShop Debug] Parsed: " + argsString);
    if (!plugin.getFilters().containsKey(player.getUniqueId())) {
      plugin.getFilters().put(player.getUniqueId(), new Filter("", "", plugin.getItems()));
    }
    if (argsString.startsWith("currentAmount")) {
      argsString = argsString.replaceAll("currentAmount", "");
      if (!(player instanceof Player))
        return "0";
      Player p = (Player) player;
      int amount = 0;
      for (MetadataValue s : p.getMetadata("deluxeshop-amount")) {
        amount = s.asInt();
        break;
      }
      if (argsString.equals("")) {
        
        amount = amount >= 2304 ? 2304 : amount;
        amount = amount <= 1 ? 1 : amount;
        return amount + "";
      }
      try {
        amount += POS_NEG.parse(argsString).intValue();
        amount = amount >= 2304 ? 2304 : amount;
        amount = amount <= 1 ? 1 : amount;
        p.setMetadata("deluxeshop-amount", new FixedMetadataValue(plugin, amount));
        return amount + "";
      } catch (Exception e) {
        return "";
      }
    }
    LinkedHashMap<String, ShopItem> tempItems = plugin.getFilters().get(player.getUniqueId()).getItems();
    if (argsString.equals("filter"))
      return plugin.getFilters().get(player.getUniqueId()).getFilter();
    if (argsString.equals("antifilter"))
      return plugin.getFilters().get(player.getUniqueId()).getAntiFilter();
    if (argsString.equals("formatfilter")) {
      return formatFilter(plugin.getFilters().get(player.getUniqueId()).getFilter());
    }
    if (argsString.equals("formatantifilter")) {
      return formatFilter(plugin.getFilters().get(player.getUniqueId()).getAntiFilter());
    }
    if (argsString.startsWith("pages:")) {
      double items = Integer.parseInt(argsString.split(":")[1]);
      return (int) Math.ceil(tempItems.size() / items) + "";
    }
    if (argsString.startsWith("buyMax:")) {
      argsString = argsString.replace("buyMax:", "");
      if (!(player instanceof Player))
        return "0";
      Player p = (Player) player;
      int max = (int) (DeluxeShop.getEconomy().getBalance(player)
          / tempItems.get(plugin.getItemName(player, argsString)).getBuy());
      max = max >= 2304 ? 2304 : max;
      p.setMetadata("deluxeshop-amount", new FixedMetadataValue(plugin, max));
      return max + "";
    }
    if (argsString.startsWith("sellMax:")) {
      argsString = argsString.replace("sellMax:", "");
      if (!(player instanceof Player))
        return "0";
      Player p = (Player) player;
      ItemStack blankItem = new ItemStack(Material.getMaterial(plugin.getItemName(player, argsString).toUpperCase()));
      int amount = 0;
      for (int i = 0; i <= 40; i++) {
        if (p.getInventory().getItem(i) != null && p.getInventory().getItem(i).isSimilar(blankItem)) {
          amount += p.getInventory().getItem(i).getAmount();
        }
      }
      amount = amount >= 2304 ? 2304 : amount;
      amount = amount <= 1 ? 1 : amount;
      p.setMetadata("deluxeshop-amount", new FixedMetadataValue(plugin, amount));
      return amount + "";
    }
    
    String[] args = argsString.split(",");
    if (args[0].startsWith("math_")) {
      args[0] = PlaceholderAPI.setPlaceholders(player, "%" + args[0] + "%");
    }
    String name = plugin.getItemName(player, args[0]);
    if (args[1].equals("sell")) {
      return DECIMAL_FORMAT.format(plugin.getItems().get(name).getSell(player));
    } else if (args[1].equals("name")) {
      if (args.length > 2 && args[2].equals("formatted")) {
        String tempName = "";
        for (String s : name.split("_")) {
          tempName += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return tempName.substring(0, tempName.length() - 1);
      }
      return name;
    } else if (args[1].equals("mat")) {
      return name.toUpperCase();
    } else if (args[1].equals("buy")) {
      return DECIMAL_FORMAT.format(plugin.getItems().get(name).getBuy());
    }
    return "";
  }
  
  private String formatFilter(String filter) {
    String out = "";
    for (String s : filter.split(",")) {
      s = s.trim().replaceAll(" ", "_");
      String temp = "";
      for (String t : s.split("_")) {
        if (!t.equals(""))
          temp += t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase() + " ";
      }
      out += temp.substring(0, temp.length() - 1) + ", ";
    }
    return out.substring(0, out.length() - 2);
  }
  
}
