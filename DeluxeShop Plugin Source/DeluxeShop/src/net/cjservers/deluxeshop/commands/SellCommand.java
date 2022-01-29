package net.cjservers.deluxeshop.commands;

import java.util.LinkedHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.cjservers.deluxeshop.DeluxeShop;
import net.cjservers.deluxeshop.ShopItem;

public class SellCommand implements CommandExecutor {
  
  LinkedHashMap<String, ShopItem> allItems;
  
  public SellCommand(LinkedHashMap<String, ShopItem> items) {
    this.allItems = items;
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("deluxeshopsell")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("&cYou must be a player to use this command");
        return true;
      }
      Player p = (Player) sender;
      if (sender.hasPermission("deluxeshop.sell")) {
        if (sender.hasPermission("deluxeshop.sell.hand") && (args.length >= 1) && (args[0].equalsIgnoreCase("hand"))) {
          String itemName = p.getInventory().getItemInMainHand().getType().toString().toLowerCase();
          if (allItems.containsKey(itemName)
              && p.getInventory().getItemInMainHand()
                  .isSimilar(new ItemStack(Material.getMaterial(itemName.toUpperCase())))) {
            double sell = allItems.get(itemName).getSell(p);
            int amount = p.getInventory().getItemInMainHand().getAmount();
            p.getInventory().getItemInMainHand().setAmount(0);
            DeluxeShop.getEconomy().depositPlayer(p, sell * amount);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6Successfully sold &a"
                    + amount
                    + " "
                    + DeluxeShop.formatName(itemName)
                    + " &6for &a"
                    + DeluxeShop.DECIMAL_FORMAT.format(amount * sell)));
            return true;
          }
          p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Nothing to sell!"));
          return true;
        } else if (sender.hasPermission("deluxeshop.sell.handall")
            && (args.length >= 1)
            && (args[0].equalsIgnoreCase("handall"))) {
          String itemName = p.getInventory().getItemInMainHand().getType().toString().toLowerCase();
          if (allItems.containsKey(itemName)) {
            double sell = allItems.get(itemName).getSell(p);
            ItemStack blankItem = new ItemStack(Material.getMaterial(itemName.toUpperCase()));
            int amount = 0;
            for (int i = 0; i <= 40; i++) {
              if (p.getInventory().getItem(i) != null && p.getInventory().getItem(i).isSimilar(blankItem)) {
                amount += p.getInventory().getItem(i).getAmount();
                p.getInventory().setItem(i, null);
              }
            }
            if (amount == 0) {
              p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Nothing to sell!"));
              return true;
            }
            DeluxeShop.getEconomy().depositPlayer(p, sell * amount);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6Successfully sold &a"
                    + amount
                    + " "
                    + DeluxeShop.formatName(itemName)
                    + " &6for &a"
                    + DeluxeShop.DECIMAL_FORMAT.format(amount * sell)));
          }
          return true;
        } else if (sender.hasPermission("deluxeshop.sell.all")
            && (args.length >= 1)
            && (args[0].equalsIgnoreCase("all"))) {
          double totalValue = 0;
          double totalAmount = 0;
          for (int i = 0; i <= 40; i++) {
            if (p.getInventory().getItem(i) == null) {
              continue;
            }
            String itemName = p.getInventory().getItem(i).getType().toString().toLowerCase();
            if (!allItems.containsKey(itemName)) {
              continue;
            }
            double sell = allItems.get(itemName).getSell(p);
            ItemStack blankItem = new ItemStack(Material.getMaterial(itemName.toUpperCase()));
            if (p.getInventory().getItem(i).isSimilar(blankItem)) {
              int amount = p.getInventory().getItem(i).getAmount();
              p.getInventory().setItem(i, null);
              DeluxeShop.getEconomy().depositPlayer(p, sell * amount);
              totalAmount += amount;
              totalValue += (sell * amount);
            }
          }
          if (totalAmount == 0) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Nothing to sell!"));
            return true;
          }
          p.sendMessage(ChatColor.translateAlternateColorCodes('&',
              "&6Successfully sold &a"
                  + totalAmount
                  + " items &6for &a"
                  + DeluxeShop.DECIMAL_FORMAT.format(totalValue)));
          return true;
        } else if (sender.hasPermission("deluxeshop.sell.menu")
            && (args.length >= 1)
            && (args[0].equalsIgnoreCase("menu"))) {
          p.getOpenInventory().close();
          DeluxeShop.sellMenu.openInventory(p);
          return true;
        } else if (args.length >= 2 && allItems.containsKey(args[0].toLowerCase())) {
          String itemName = args[0].toLowerCase();
          int amountLeft = 0;
          try {
            amountLeft = Integer.parseInt(args[1]);
          } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Amount"));
            return true;
          }
          double sell = allItems.get(itemName).getSell(p);
          ItemStack blankItem = new ItemStack(Material.getMaterial(itemName.toUpperCase()));
          int amount = 0;
          for (int i = 0; i <= 40; i++) {
            if (amountLeft > 0
                && p.getInventory().getItem(i) != null
                && p.getInventory().getItem(i).isSimilar(blankItem)) {
              int itemAmount = p.getInventory().getItem(i).getAmount();
              if (itemAmount >= amountLeft) {
                p.getInventory().getItem(i).setAmount(itemAmount - amountLeft);
                amount += amountLeft;
                amountLeft = 0;
              } else {
                p.getInventory().setItem(i, null);
                amountLeft -= itemAmount;
                amount += itemAmount;
              }
            }
          }
          if (amount == 0) {
            p.sendMessage(
                ChatColor.translateAlternateColorCodes('&', "&8No " + DeluxeShop.formatName(itemName) + " to sell!"));
            return true;
          }
          DeluxeShop.getEconomy().depositPlayer(p, sell * amount);
          p.sendMessage(ChatColor.translateAlternateColorCodes('&',
              "&6Successfully sold &a"
                  + amount
                  + " "
                  + DeluxeShop.formatName(itemName)
                  + " &6for &a"
                  + DeluxeShop.DECIMAL_FORMAT.format(amount * sell)));
          return true;
        }
        helpMenu(sender);
        return true;
      }
      return true;
    }
    return false;
    
  }
  
  private void helpMenu(CommandSender sender) {
    sender.sendMessage(ChatColor.GOLD + "-------------DeluxeShop v3.0.0-------------");
    if (sender.hasPermission("deluxeshop.sell"))
      sender.sendMessage("/sell <item> <amount>  -- Sells a certain amount of an item");
    if (sender.hasPermission("deluxeshop.sell.hand"))
      sender.sendMessage("/sell hand -- Sells the item in your main hand");
    if (sender.hasPermission("deluxeshop.sell.handall"))
      sender.sendMessage("/sell handall -- Sells all items in your inventory that match the item in your main hand");
    if (sender.hasPermission("deluxeshop.sell.all"))
      sender.sendMessage("/sell all -- Sells everything in your inventory that can be sold");
    if (sender.hasPermission("deluxeshop.sell.menu"))
      sender.sendMessage("/sell menu -- Opens a menu for you to put items into and sell");
  }
  
}