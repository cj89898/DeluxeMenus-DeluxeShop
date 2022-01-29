package net.cjservers.deluxeshop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Filter {
  
  private String filter = "";
  private String antiFilter = "";
  private LinkedHashMap<String, ShopItem> filterItems;
  private LinkedHashMap<String, ShopItem> items;
  
  public Filter(String filter, String antiFilter, LinkedHashMap<String, ShopItem> items) {
    filterItems = items;
    setFilter(filter, items);
    setAntiFilter(antiFilter);
  }
  
  public String getFilter() {
    return filter;
  }
  
  public String getAntiFilter() {
    return antiFilter;
  }
  
  public LinkedHashMap<String, ShopItem> getItems() {
    return items == null ? filterItems : items;
  }
  
  public void setFilter(String filter, LinkedHashMap<String, ShopItem> items) {
    if (filter.equals(getFilter()))
      return;
    this.filter = filter;
    if (filter.equals("")) {
      filterItems = items;
      setAntiFilter(getAntiFilter());
      return;
    }
    //------- Create Filtered List 
    LinkedHashMap<String, ShopItem> tempItems = new LinkedHashMap<String, ShopItem>();
    tempItems.put("air", new ShopItem(0.0, 0.0, new ArrayList<>()));
    for (Entry<String, ShopItem> i : items.entrySet()) {
      for (String s : filter.split(",")) {
        s = s.trim().replaceAll(" ", "_");
        if (i.getKey().contains(s) || i.getValue().getTags().contains(s)) {
          tempItems.put(i.getKey(), i.getValue());
        }
      }
    }
    //-------
    
    filterItems = tempItems;
    setAntiFilter(getAntiFilter());
  }
  
  public void setAntiFilter(String antiFilter) {
    if (antiFilter.equals("")) {
      items = null;
      return;
    }
    this.antiFilter = antiFilter;
    @SuppressWarnings("unchecked")
    LinkedHashMap<String, ShopItem> tempItems = (LinkedHashMap<String, ShopItem>) filterItems.clone();
    for (Entry<String, ShopItem> i : filterItems.entrySet()) {
      for (String s : antiFilter.split(",")) {
        s = s.trim().replaceAll(" ", "_");
        if (i.getKey().contains(s) || i.getValue().getTags().contains(s)) {
          tempItems.remove(i.getKey());
        }
      }
    }
    //-------
    items = tempItems;
  }
  
}
