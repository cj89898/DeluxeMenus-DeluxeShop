function items(){
    if(!args[1] == "buy" && !args[1] == "sell" && !args[1] == "name")
        return "";
    var dataLoc = "items." + args[0] + "." + args[1];
    var data = Data.get(dataLoc);
    if(args[1] == "sell"){
        var selling = Data.get("selling");
        if(selling == "individual"){
            return data;
        }else{
            var sellMult = parseFloat(selling);
            dataLoc = "items." + args[0] + ".buy";
            return parseFloat(Data.get(dataLoc))*sellMult;
        }
    }
    return data;
}
items();