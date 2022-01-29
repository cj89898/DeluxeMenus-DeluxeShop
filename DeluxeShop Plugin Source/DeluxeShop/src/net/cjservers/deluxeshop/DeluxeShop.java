package net.cjservers.deluxeshop;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.PlaceholderAPI;
import net.cjservers.deluxeshop.commands.CommandTabCompleter;
import net.cjservers.deluxeshop.commands.SearchCommand;
import net.cjservers.deluxeshop.commands.SellCommand;
import net.milkbowl.vault.economy.Economy;

public class DeluxeShop extends JavaPlugin {
  
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
  
  private File confYml;
  private YamlConfiguration conf;
  private static boolean sellIndividual;
  private static boolean sellPlaceholder;
  private static String sellPlaceholderMulti;
  private static double sellMulti;
  
  private LinkedHashMap<String, ShopItem> items;
  private Map<UUID, Filter> filters;
  private static Economy econ;
  
  public static SellMenu sellMenu;
  
  @Override
  public void onEnable() {
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    new DeluxeShopExpansion(this).register();
    confYml = new File(getDataFolder(), "config.yml");
    conf = YamlConfiguration.loadConfiguration(confYml);
    if (!(confYml.exists())) {
      saveDefaultConfig();
      getLogger().info("[DeluxeShop] - Created config.yml");
    }
    
    try {
      String selling = conf.getString("selling");
      sellIndividual = selling == null ? false : selling.equalsIgnoreCase("individual");
      if (PlaceholderAPI.containsPlaceholders(selling)) {
        sellPlaceholder = true;
        sellPlaceholderMulti = selling;
      }
      if (!sellIndividual && !sellPlaceholder)
        sellMulti = conf.getDouble("selling");
    } catch (Exception e) {
      e.printStackTrace();
      getLogger().info("[DeluxeShop] - Could not set sell multiplier, reverting to individual");
      sellIndividual = true;
      return;
    }
    
    filters = new HashMap<>();
    
    items = new LinkedHashMap<String, ShopItem>();
    items.put("air", new ShopItem(0.0, 0.0, new ArrayList<>()));
    for (String item : conf.getConfigurationSection("items").getKeys(false)) {
      double buy = Double.parseDouble(conf.getString("items." + item + ".buy"));
      double sell = 0;
      if (sellIndividual) {
        sell = Double.parseDouble(conf.getString("items." + item + ".sell"));
      }
      List<String> tags = conf.getStringList("items." + item + ".tags");
      for (int i = 0; i < tags.size(); i++) {
        tags.set(i, tags.get(i).toLowerCase());
      }
      items.put(item.toLowerCase(), new ShopItem(buy, sell, tags));
    }
    CommandTabCompleter tabCompleter = new CommandTabCompleter();
    
    if (setupEconomy()) {
      getCommand("deluxeshopsell").setExecutor(new SellCommand(items));
      getCommand("deluxeshopsell").setTabCompleter(tabCompleter);
      sellMenu = new SellMenu(items);
      getServer().getPluginManager().registerEvents(sellMenu, this);
    } else {
      getLogger().info("[DeluxeShop] - Vault not found, sell commands disabled");
    }
    getCommand("search").setExecutor(new SearchCommand(this));
  }
  
  @Override
  public void onDisable() {
    
  }
  
  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return econ != null;
  }
  
  public static Economy getEconomy() {
    return econ;
  }
  
  public Map<UUID, Filter> getFilters() {
    return filters;
  }
  
  public static String formatName(String name) {
    String tempName = "";
    for (String s : name.split("_")) {
      tempName += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
    }
    return tempName.substring(0, tempName.length() - 1);
  }
  
  public LinkedHashMap<String, ShopItem> getItems() {
    return items;
  }
  
  public static boolean sellInvidiual() {
    return sellIndividual;
  }
  
  public static boolean sellPlaceholder() {
    return sellPlaceholder;
  }
  
  public static String sellPlaceholderMulti() {
    return sellPlaceholderMulti;
  }
  
  public static double sellMulti() {
    return sellMulti;
  }
  
}
