package net.cjservers.deluxeshop;

import java.util.List;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.PlaceholderAPI;

public class ShopItem {
  
  private double buy;
  private double sell;
  private List<String> tags;
  
  public ShopItem(double buy, double sell, List<String> tags) {
    this.buy = buy;
    this.sell = sell;
    this.tags = tags;
  }
  
  public double getBuy() {
    return buy;
  }
  
  public double getSell(OfflinePlayer player) {
    if (DeluxeShop.sellInvidiual()) {
      return sell;
    } else if (DeluxeShop.sellPlaceholder()) {
      return getBuy() * Double.parseDouble(PlaceholderAPI.setPlaceholders(player, DeluxeShop.sellPlaceholderMulti()));
    } else {
      return getBuy() * DeluxeShop.sellMulti();
    }
  }
  
  public List<String> getTags() {
    return tags;
  }
  
}
