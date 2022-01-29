package net.cjservers.deluxeshop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.cjservers.deluxeshop.DeluxeShop;
import net.cjservers.deluxeshop.utilities.Utils;

public class DeluxeShopCommand implements CommandExecutor {
  
  private final DeluxeShop deluxeshop;
  
  public DeluxeShopCommand(DeluxeShop plugin) {
    this.deluxeshop = plugin;
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("deluxeshop")) {
      if (sender.hasPermission("deluxeshop.admin") && args.length > 0 && args[0].equals("reload")) {
        deluxeshop.reload();
        Utils.sendTranslatedMessage(sender, "&aConfig reloaded");
        return true;
      }
      sender.sendMessage("Deluxeshop v" + deluxeshop.getDescription().getVersion());
      sender.sendMessage("/deluxeshop reload -- Reloads configs");
      return true;
    }
    return false;
  }
  
}