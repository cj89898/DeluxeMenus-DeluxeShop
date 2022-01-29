var currentMeta = parseInt("%deluxemenus_meta_dmshop-currentAmount_INTEGER_1%");
if(args[0].startsWith("{") && args[0].endsWith("}"))
    args[0] = PlaceholderAPI.static.setPlaceholders(BukkitPlayer, "%"+args[0].substring(1, args[0].length-1)+"%");
var difference = parseInt(args[0]);
if(currentMeta == 1 && difference > 1){
    currentMeta = difference;
}else{
    currentMeta += difference;
}
if(currentMeta <= 0)
    currentMeta = 1;
if(currentMeta >= 2304)
    currentMeta = 2304;
currentMeta.toFixed(0);