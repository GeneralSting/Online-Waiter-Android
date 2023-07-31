package com.example.onlinewaiter.Functions;


import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CategoryDrink;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortHashMap {
    public static LinkedHashMap<String, CafeBillDrink>  sortCafeBillsMap (HashMap<String, CafeBillDrink> cafeBillsMap) {
        List<Map.Entry<String, CafeBillDrink>> entryList = new ArrayList<>(cafeBillsMap.entrySet());
        entryList.sort(new Comparator<Map.Entry<String, CafeBillDrink>>() {
            @Override
            public int compare(Map.Entry<String, CafeBillDrink> firstEntry, Map.Entry<String, CafeBillDrink> secondEntry) {
                String firstDrinkName = firstEntry.getValue().getDrinkName();
                String secondDrinkName = secondEntry.getValue().getDrinkName();
                return firstDrinkName.compareTo(secondDrinkName);
            }
        });
        LinkedHashMap<String, CafeBillDrink> sortedDrinks = new LinkedHashMap<>();
        for (Map.Entry<String, CafeBillDrink> entry : entryList) {
            sortedDrinks.put(entry.getKey(), entry.getValue());
        }
        return sortedDrinks;
    }

    public static LinkedHashMap<String, CategoryDrink>  sortCategoryDrinksMap (HashMap<String, CategoryDrink> categoryDrinksMap) {
        List<Map.Entry<String, CategoryDrink>> entryList = new ArrayList<>(categoryDrinksMap.entrySet());
        entryList.sort(new Comparator<Map.Entry<String, CategoryDrink>>() {
            @Override
            public int compare(Map.Entry<String, CategoryDrink> firstEntry, Map.Entry<String, CategoryDrink> secondEntry) {
                String firstDrinkName = firstEntry.getValue().getCategoryDrinkName();
                String secondDrinkName = secondEntry.getValue().getCategoryDrinkName();
                return firstDrinkName.compareTo(secondDrinkName);
            }
        });
        LinkedHashMap<String, CategoryDrink> sortedDrinks = new LinkedHashMap<>();
        for (Map.Entry<String, CategoryDrink> entry : entryList) {
            sortedDrinks.put(entry.getKey(), entry.getValue());
        }
        return sortedDrinks;
    }
}
