package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class CafeCurrentOrder {
    String currentOrderDatetime, currentOrderTotalPrice, currentOrderDelivererEmployee, currentOrderMakerEmployee;
    int currentOrderProductAmount, currentOrderTableNumber;
    HashMap<String, CafeBillDrink> currentOrderDrinks;

    public CafeCurrentOrder() {}

    public CafeCurrentOrder(String currentOrderDatetime, String currentOrderTotalPrice, String currentOrderDelivererEmployee, String currentOrderMakerEmployee,
                            int currentOrderProductAmount, int currentOrderTableNumber, HashMap<String, CafeBillDrink> currentOrderDrinks) {
        this.currentOrderDatetime = currentOrderDatetime;
        this.currentOrderTotalPrice = currentOrderTotalPrice;
        this.currentOrderDelivererEmployee = currentOrderDelivererEmployee;
        this.currentOrderMakerEmployee = currentOrderMakerEmployee;
        this.currentOrderProductAmount = currentOrderProductAmount;
        this.currentOrderTableNumber = currentOrderTableNumber;
        this.currentOrderDrinks = currentOrderDrinks;
    }

    public CafeCurrentOrder(String currentOrderDatetime, String currentOrderTotalPrice, String currentOrderDelivererEmployee,
                            int currentOrderProductAmount, int currentOrderTableNumber, HashMap<String, CafeBillDrink> currentOrderDrinks) {
        this.currentOrderDatetime = currentOrderDatetime;
        this.currentOrderTotalPrice = currentOrderTotalPrice;
        this.currentOrderDelivererEmployee = currentOrderDelivererEmployee;
        this.currentOrderProductAmount = currentOrderProductAmount;
        this.currentOrderTableNumber = currentOrderTableNumber;
        this.currentOrderDrinks = currentOrderDrinks;
    }

    public String getCurrentOrderDatetime() {
        return currentOrderDatetime;
    }

    public void setCurrentOrderDatetime(String currentOrderDatetime) {
        this.currentOrderDatetime = currentOrderDatetime;
    }

    public String getCurrentOrderTotalPrice() {
        return currentOrderTotalPrice;
    }

    public void setCurrentOrderTotalPrice(String currentOrderTotalPrice) {
        this.currentOrderTotalPrice = currentOrderTotalPrice;
    }

    public String getCurrentOrderDelivererEmployee() {
        return currentOrderDelivererEmployee;
    }

    public void setCurrentOrderDelivererEmployee(String currentOrderDelivererEmployee) {
        this.currentOrderDelivererEmployee = currentOrderDelivererEmployee;
    }

    public String getCurrentOrderMakerEmployee() {
        return currentOrderMakerEmployee;
    }

    public void setCurrentOrderMakerEmployee(String currentOrderMakerEmployee) {
        this.currentOrderMakerEmployee = currentOrderMakerEmployee;
    }

    public int getCurrentOrderProductAmount() {
        return currentOrderProductAmount;
    }

    public void setCurrentOrderProductAmount(int currentOrderProductAmount) {
        this.currentOrderProductAmount = currentOrderProductAmount;
    }

    public int getCurrentOrderTableNumber() {
        return currentOrderTableNumber;
    }

    public void setCurrentOrderTableNumber(int currentOrderTableNumber) {
        this.currentOrderTableNumber = currentOrderTableNumber;
    }

    public HashMap<String, CafeBillDrink> getCurrentOrderDrinks() {
        return currentOrderDrinks;
    }

    public void setCurrentOrderDrinks(HashMap<String, CafeBillDrink> currentOrderDrinks) {
        this.currentOrderDrinks = currentOrderDrinks;
    }
}
