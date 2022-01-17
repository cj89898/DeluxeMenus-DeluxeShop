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
    }else if(args[1] == "name"){
        if(args.length > 2 && args[2] == "formatted")
            return data.replaceAll('_', ' ');
    }
    return data;
}
items();