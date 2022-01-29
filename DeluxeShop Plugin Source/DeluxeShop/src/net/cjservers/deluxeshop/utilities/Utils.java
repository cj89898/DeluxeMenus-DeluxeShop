package net.cjservers.deluxeshop.utilities;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.cjservers.deluxeshop.DeluxeShop;
import net.md_5.bungee.api.ChatColor;

public class Utils {
  
  private final DeluxeShop plugin;
  
  public Utils(DeluxeShop plugin) {
    this.plugin = plugin;
  }
  
  private static final String DIRECTORY = "plugins/DeluxeShop/";
  
  private static File getFile(String name) throws IOException {
    File file = new File(DIRECTORY, name);
    
    return file.createNewFile() ? file : file.exists() ? file : null;
  }
  
  public static FileConfiguration getConfiguration(String name) {
    try {
      File file = getFile(name);
      
      if (file != null) {
        return YamlConfiguration.loadConfiguration(file);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void save(FileConfiguration configuration, String name) {
    try {
      File file = getFile(name);
      
      if (file != null) {
        configuration.save(file);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void reload(FileConfiguration configuration, String name) {
    try {
      File file = getFile(name);
      
      if (file != null) {
        configuration = YamlConfiguration.loadConfiguration(file);
        // configuration.save(file);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void sendTranslatedMessageFromTag(Player p, String m) {
    for (String tag : DeluxeShop.getMessages().keySet()) {
      if (tag.equals(m)) {
        sendTranslatedMessage(p, DeluxeShop.getMessages().get(tag));
        return;
      }
    }
  }
  
  public static void sendTaggedPlaceHolderMessage(Player p, String m, String ph, String value) {
    for (String tag : DeluxeShop.getMessages().keySet()) {
      if (tag.equals(m)) {
        m = DeluxeShop.getMessages().get(tag);
        m = m.replaceAll(ph, value);
        sendTranslatedMessage(p, m);
        return;
      }
    }
  }
  
  public static void sendTaggedPlaceHolderMessage(CommandSender sender, String m, String ph, String value) {
    for (String tag : DeluxeShop.getMessages().keySet()) {
      if (tag.equals(m)) {
        m = DeluxeShop.getMessages().get(tag);
        m = m.replaceAll(ph, value);
        sendTranslatedMessage(sender, m);
        return;
      }
    }
  }
  
  public static void sendTaggedPlaceHolderMessage(Player p, String m, List<String> ph, List<String> value) {
    for (String tag : DeluxeShop.getMessages().keySet()) {
      if (tag.equals(m)) {
        m = DeluxeShop.getMessages().get(tag);
        for (int i = 0; i < ph.size(); i++) {
          m = m.replaceAll(ph.get(i), value.get(i));
        }
        sendTranslatedMessage(p, m);
        return;
      }
    }
  }
  
  public static void sendTaggedPlaceHolderMessage(CommandSender s, String m, List<String> ph, List<String> value) {
    for (String tag : DeluxeShop.getMessages().keySet()) {
      if (tag.equals(m)) {
        m = DeluxeShop.getMessages().get(tag);
        for (int i = 0; i < ph.size(); i++) {
          m = m.replaceAll(ph.get(i), value.get(i));
        }
        sendTranslatedMessage(s, m);
        return;
      }
    }
  }
  
  public static void sendTranslatedMessageFromTag(CommandSender s, String m) {
    for (String tag : DeluxeShop.getMessages().keySet()) {
      if (tag.equals(m)) {
        sendTranslatedMessage(s, DeluxeShop.getMessages().get(tag));
        return;
      }
    }
  }
  
  public static void sendTranslatedMessage(CommandSender s, String m) {
    if (s instanceof OfflinePlayer) {
      m = PlaceholderAPI.setPlaceholders((OfflinePlayer) s, m);
    }
    m = ChatColor.translateAlternateColorCodes('&', m);
    s.sendMessage(m);
  }
  
  public static String formatName(String name) {
    String tempName = "";
    for (String s : name.split("_")) {
      tempName += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
    }
    return tempName.substring(0, tempName.length() - 1);
  }
  
}
