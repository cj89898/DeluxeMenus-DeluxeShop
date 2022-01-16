function items(){
    if(!args[1] == "price" && args[1] == "name")
        return "";
    var dataLoc = "items." + args[0] + "." + args[1];
    return Data.get(dataLoc);
}
items();