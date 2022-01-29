package net.cjservers.deluxeshop;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class DeluxeShopExpansion extends PlaceholderExpansion {
  
  private final DeluxeShop plugin;
  
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
  
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
    if (!plugin.getFilters().containsKey(player.getUniqueId())) {
      plugin.getFilters().put(player.getUniqueId(), new Filter("", "", plugin.getItems()));
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
    String[] args = argsString.split(",");
    String name = "";
    if (args[0].startsWith("math_")) {
      args[0] = PlaceholderAPI.setPlaceholders(player, "%" + args[0] + "%");
    }
    try {
      name = (String) tempItems.keySet().toArray()[Integer.parseInt(args[0])];
    } catch (NumberFormatException numError) {
      String[] nameArr = args[0].toLowerCase().split("_");
      for (String s : nameArr) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1);
        name += s + "_";
      }
      if (name.endsWith("_"))
        name = name.substring(0, name.length() - 1);
    } catch (ArrayIndexOutOfBoundsException oobError) {
      return "";
    }
    if (args[1].equals("sell")) {
      return DECIMAL_FORMAT.format(tempItems.get(name).getSell(player));
    } else if (args[1].equals("name")) {
      if (args.length > 2 && args[2].equals("formatted")) {
        String tempName = "";
        for (String s : name.split("_")) {
          tempName += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return tempName.substring(0, tempName.length() - 1);
      }
      return name;
    } else if (args[1].equals("buy")) {
      return DECIMAL_FORMAT.format(tempItems.get(name).getBuy());
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
