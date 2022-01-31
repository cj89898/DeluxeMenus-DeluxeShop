package net.cjservers.deluxeshop;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.PlaceholderAPI;
import net.cjservers.deluxeshop.commands.CommandTabCompleter;
import net.cjservers.deluxeshop.commands.DeluxeShopCommand;
import net.cjservers.deluxeshop.commands.SearchCommand;
import net.cjservers.deluxeshop.commands.SellCommand;
import net.cjservers.deluxeshop.utilities.Utils;
import net.milkbowl.vault.economy.Economy;

public class DeluxeShop extends JavaPlugin {
  
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
  
  private File confYml;
  private FileConfiguration conf;
  private File msgYml;
  private FileConfiguration msgConf;
  
  private static boolean sellIndividual;
  private static boolean sellPlaceholder;
  private static String sellPlaceholderMulti;
  private static double sellMulti;
  
  private LinkedHashMap<String, ShopItem> items;
  private Map<UUID, Filter> filters;
  
  private ArrayList<ItemStack> sellMenuItems;
  private String sellMenuTitle;
  private static Economy econ;
  
  public static SellMenu sellMenu;
  public static Utils utils;
  
  public static Map<String, String> messages;
  
  @Override
  public void onEnable() {
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    new DeluxeShopExpansion(this).register();
    utils = new Utils(this);
    reload();
    
    CommandTabCompleter tabCompleter = new CommandTabCompleter();
    
    getCommand("deluxeshop").setExecutor(new DeluxeShopCommand(this));
    getCommand("deluxeshop").setTabCompleter(tabCompleter);
    
    if (setupEconomy()) {
      getCommand("deluxeshopsell").setExecutor(new SellCommand(items));
      getCommand("deluxeshopsell").setTabCompleter(tabCompleter);
      sellMenu = new SellMenu(items, sellMenuItems, sellMenuTitle);
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
  
  public static Map<String, String> getMessages() {
    return messages;
  }
  
  public void fixConf() {
    if (!(confYml.exists())) {
      saveDefaultConfig();
      getLogger().info("[DeluxeShop] - Created config.yml");
    }
    if (!(msgYml.exists())) {
      try {
        msgYml.createNewFile();
        System.out.println("[DeluxeShop] - Created messages.yml");
      } catch (IOException e) {
        getLogger().severe(ChatColor.RED + "[DeluxeShop] - Could not create messages.yml");
      }
    }
  }
  
  private ItemStack createGuiItem(final Material material, final String name, final List<String> lore) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();
    
    // Set the name of the item
    meta.setDisplayName(name);
    
    // Set the lore of the item
    meta.setLore(lore);
    
    item.setItemMeta(meta);
    
    return item;
  }
  
  public void reload() {
    confYml = new File(getDataFolder(), "config.yml");
    msgYml = new File(getDataFolder(), "messages.yml");
    
    fixConf();
    conf = Utils.getConfiguration("config.yml");
    msgConf = Utils.getConfiguration("messages.yml");
    
    initMessages();
    initSellMenu();
    initConfigOpts();
    
    if (sellMenu != null) {
      sellMenu.setMenuItems(sellMenuItems);
      sellMenu.setTitle(sellMenuTitle);
    }
  }
  
  private void initMessages() {
    messages = new HashMap<String, String>();
    if (msgConf.getString("no-item-to-sell") == null)
      msgConf.set("no-item-to-sell", "&8No &6%item% &8to sell!");
    if (msgConf.getString("nothing-to-sell") == null)
      msgConf.set("nothing-to-sell", "&8Nothing to sell!");
    if (msgConf.getString("successful-sell-hand") == null)
      msgConf.set("successful-sell-hand", "&6Successfully sold &a%amount% %item% &6for &a$%value%");
    if (msgConf.getString("successful-sell-all") == null)
      msgConf.set("successful-sell-all", "&6Successfully sold &a%amount% &6items for &a$%value%");
    if (msgConf.getString("invalid-amount") == null)
      msgConf.set("invalid-amount", "&cInvalid Amount");
    if (msgConf.getString("search-other-clear") == null)
      msgConf.set("search-other-clear", "&8Cleared Search for: &a%player%");
    if (msgConf.getString("search-other") == null)
      msgConf.set("search-other", "&8Search for: &a%player% &8set to: &a%filter%");
    if (msgConf.getString("console-needs-player") == null)
      msgConf.set("console-needs-player", "&cYou must be a player to use this command");
    Utils.save(msgConf, "messages.yml");
    
    for (String e : msgConf.getConfigurationSection("").getKeys(false)) {
      messages.put(e, msgConf.getString(e));
    }
  }
  
  private void initSellMenu() {
    if (conf.getString("sellmenu.title") == null) {
      conf.set("sellmenu.title", "Sell Items");
      Utils.save(conf, "config.yml");
    }
    if (conf.get("sellmenu.items") == null) {
      String c = "sellmenu.items.cancel.";
      conf.set(c + ".material", "DIAMOND_SWORD");
      conf.set(c + "display_name", "Cancel");
      conf.set(c + "lore", new ArrayList<String>());
      //Divider Item
      c = "sellmenu.items.divider.";
      conf.set(c + ".material", "DIAMOND_SWORD");
      conf.set(c + "display_name", " ");
      conf.set(c + "lore", new ArrayList<String>());
      //Sell Item
      c = "sellmenu.items.sell.";
      conf.set(c + ".material", "DIAMOND_SWORD");
      conf.set(c + "display_name", "Sell");
      conf.set(c + "lore", new ArrayList<String>());
      Utils.save(conf, "config.yml");
    }
    sellMenuItems = new ArrayList<ItemStack>();
    //Cancel Item
    String c = "sellmenu.items.cancel.";
    Material mat = Material.matchMaterial(conf.getString(c + "material").toUpperCase());
    String name = ChatColor.translateAlternateColorCodes('&', conf.getString(c + "display_name"));
    List<String> lore = conf.getStringList(c + ".lore");
    sellMenuItems.add(createGuiItem(mat, name, lore));
    
    //Divider Item
    c = "sellmenu.items.divider.";
    mat = Material.matchMaterial(conf.getString(c + "material").toUpperCase());
    name = ChatColor.translateAlternateColorCodes('&', conf.getString(c + "display_name"));
    lore = conf.getStringList(c + ".lore");
    sellMenuItems.add(createGuiItem(mat, name, lore));
    
    //Sell Item
    c = "sellmenu.items.sell.";
    mat = Material.matchMaterial(conf.getString(c + "material").toUpperCase());
    name = ChatColor.translateAlternateColorCodes('&', conf.getString(c + "display_name"));
    lore = conf.getStringList(c + ".lore");
    sellMenuItems.add(createGuiItem(mat, name, lore));
    
    sellMenuTitle = conf.getString("sellmenu.title");
  }
  
  private void initConfigOpts() {
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
  }
  
}
