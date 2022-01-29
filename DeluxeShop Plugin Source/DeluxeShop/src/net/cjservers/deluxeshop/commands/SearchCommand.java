package net.cjservers.deluxeshop.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.cjservers.deluxeshop.DeluxeShop;
import net.cjservers.deluxeshop.Filter;
import net.cjservers.deluxeshop.utilities.Utils;

public class SearchCommand implements CommandExecutor {
  
  private final DeluxeShop deluxeshop;
  
  public SearchCommand(DeluxeShop plugin) {
    this.deluxeshop = plugin;
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("search")) {
      if (!(sender instanceof Player)) {
        if (args.length < 1) {
          Utils.sendTranslatedMessageFromTag(sender, "console-needs-player");
          return true;
        } else if (args.length < 2) {
          Utils.sendTranslatedMessage(sender,
              "/Search <player> <Search Query> -- Multiple filters can be seperated with a comma");
          return true;
        } else if (Bukkit.getPlayer(args[0]) != null) {
          Player p = Bukkit.getPlayer(args[0]);
          args[0] = "";
          if (args[1].equals("clear")) {
            args[0] = "clear";
            Utils.sendTaggedPlaceHolderMessage(sender, "search-other-clear", "%player%", p.getName());
          } else {
            args[0] = "";
            List<String> ph = new ArrayList<String>();
            ph.add("%player%");
            ph.add("%filter%");
            List<String> values = new ArrayList<String>();
            values.add(p.getName());
            values.add(String.join("", args).toLowerCase().trim());
            Utils.sendTaggedPlaceHolderMessage(sender, "search-other", ph, values);
          }
          String filter = String.join("", args).toLowerCase().trim();
          if (args[0].toLowerCase().equals("clear"))
            filter = "";
          setFilter(filter, p, sender);
          return true;
        }
        sender.sendMessage("Invalid player");
        return true;
      }
      Player p = (Player) sender;
      if (sender.hasPermission("deluxeshop.search")) {
        if (args.length < 1) {
          Utils.sendTranslatedMessage(sender,
              "&a/Search <search Query> -- Multiple filters can be seperated with a comma");
          return true;
        } else if (p.hasPermission("deluxeshop.search.other") && args.length >= 3 && args[0].equals("other")) {
          if (Bukkit.getPlayer(args[1]) != null) {
            p = Bukkit.getPlayer(args[1]);
            args[1] = "";
            if (args[2].equals("clear")) {
              args[0] = "clear";
              Utils.sendTaggedPlaceHolderMessage(sender, "search-other-clear", "%player%", p.getName());
            } else {
              args[0] = "";
              List<String> ph = new ArrayList<String>();
              ph.add("%player%");
              ph.add("%filter%");
              List<String> values = new ArrayList<String>();
              values.add(p.getName());
              values.add(String.join("", args).toLowerCase().trim());
              Utils.sendTaggedPlaceHolderMessage(sender, "search-other", ph, values);
            }
          } else {
            Utils.sendTranslatedMessage(sender, "&cInvalid Player");
            return true;
          }
        }
        String filter = String.join("", args).toLowerCase().trim();
        if (args[0].toLowerCase().equals("clear"))
          filter = "";
        setFilter(filter, p, sender);
      }
      return true;
    }
    return false;
  }
  
  private void setFilter(String filter, Player p, CommandSender sender) {
    if (sender.hasPermission("deluxeshop.search.antifilter") && filter.startsWith("anti:")) {
      filter = filter.replace("anti:", "");
      if (!deluxeshop.getFilters().containsKey(p.getUniqueId()))
        deluxeshop.getFilters().put(p.getUniqueId(), new Filter("", filter, deluxeshop.getItems()));
      else
        deluxeshop.getFilters().get(p.getUniqueId()).setAntiFilter(filter);
    } else {
      if (filter.startsWith("anti:"))
        filter = filter.replace("anti:", "");
      if (!deluxeshop.getFilters().containsKey(p.getUniqueId()))
        deluxeshop.getFilters().put(p.getUniqueId(), new Filter(filter, "", deluxeshop.getItems()));
      else
        deluxeshop.getFilters().get(p.getUniqueId()).setFilter(filter, deluxeshop.getItems());
    }
  }
  
}