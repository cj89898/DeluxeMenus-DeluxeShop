var cost = "deluxeshop_"+args[0]+",buy%";
cost = parseFloat(PlaceholderAPI.static.setPlaceholders(BukkitPlayer, "%"+cost));
var max = (parseFloat("%vault_eco_balance%")/cost).toFixed();
max >= 2304 ? 2304 : max;