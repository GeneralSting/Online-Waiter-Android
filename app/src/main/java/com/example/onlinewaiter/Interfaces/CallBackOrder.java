package com.example.onlinewaiter.Interfaces;

import com.example.onlinewaiter.Models.CafeBillDrink;

import java.util.HashMap;

public interface CallBackOrder {
    void updateOrderDrinks(HashMap<String, CafeBillDrink> orderDrinks);
}
