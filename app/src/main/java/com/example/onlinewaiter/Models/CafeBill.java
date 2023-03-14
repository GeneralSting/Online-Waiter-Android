package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class CafeBill {
    String cafeBillDate, cafeBillTotalPrice, cafeBillEmployee;
    int cafeBillProductAmount, cafeBillTableNumber;
    HashMap<String, CafeBillDrink> cafeBillDrinks;

    public CafeBill() {}

    public CafeBill(String cafeBillDate, String cafeBillTotalPrice, String cafeBillEmployee, int cafeBillProductAmount, int cafeBillTableNumber, HashMap<String, CafeBillDrink> cafeBillDrinks) {
        this.cafeBillDate = cafeBillDate;
        this.cafeBillTotalPrice = cafeBillTotalPrice;
        this.cafeBillEmployee = cafeBillEmployee;
        this.cafeBillProductAmount = cafeBillProductAmount;
        this.cafeBillTableNumber = cafeBillTableNumber;
        this.cafeBillDrinks = cafeBillDrinks;
    }

    public String getCafeBillDate() {
        return cafeBillDate;
    }

    public void setCafeBillDate(String cafeBillDate) {
        this.cafeBillDate = cafeBillDate;
    }

    public String getCafeBillTotalPrice() {
        return cafeBillTotalPrice;
    }

    public void setCafeBillTotalPrice(String cafeBillTotalPrice) {
        this.cafeBillTotalPrice = cafeBillTotalPrice;
    }

    public String getCafeBillEmployee() {
        return cafeBillEmployee;
    }

    public void setCafeBillEmployee(String cafeBillEmployee) {
        this.cafeBillEmployee = cafeBillEmployee;
    }

    public int getCafeBillProductAmount() {
        return cafeBillProductAmount;
    }

    public void setCafeBillProductAmount(int cafeBillProductAmount) {
        this.cafeBillProductAmount = cafeBillProductAmount;
    }

    public int getCafeBillTableNumber() {
        return cafeBillTableNumber;
    }

    public void setCafeBillTableNumber(int cafeBillTableNumber) {
        this.cafeBillTableNumber = cafeBillTableNumber;
    }

    public HashMap<String, CafeBillDrink> getCafeBillDrinks() {
        return cafeBillDrinks;
    }

    public void setCafeBillDrinks(HashMap<String, CafeBillDrink> cafeBillDrinks) {
        this.cafeBillDrinks = cafeBillDrinks;
    }
}
