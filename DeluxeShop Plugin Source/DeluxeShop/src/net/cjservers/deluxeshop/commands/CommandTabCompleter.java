package net.cjservers.deluxeshop.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleter implements TabCompleter {
  
  private static final String[] COMMANDS = { "<item> <amount>", "all", "hand", "handall", "menu" };
  
  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("deluxeshopsell") || cmd.getName().equalsIgnoreCase("sell")) {
      if (sender.hasPermission("deluxeshop.sell")) {
        List<String> list = new ArrayList<String>();
        if (args.length == 1) {
          if (args[0].equalsIgnoreCase("") || args[0].isEmpty()) {
            for (String s : COMMANDS) {
              list.add(s);
            }
          } else {
            for (String s : COMMANDS) {
              if (s.startsWith(args[0])) {
                list.add(s);
              }
            }
          }
        } else if (args.length == 2 && Material.matchMaterial(args[0].toUpperCase()) != null) {
          list.add("<amount>");
        } else {
          return null;
        }
        return list;
      }
    } else if (cmd.getName().equalsIgnoreCase("deluxeshop")) {
      List<String> list = new ArrayList<String>();
      if (args.length == 1) {
        list.add("reload");
        return list;
      }
    }
    return null;
  }
}
