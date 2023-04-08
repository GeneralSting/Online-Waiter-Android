package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class CafeBill {
    String cafeBillDate, cafeBillTotalPrice, cafeBillDelivererEmployee, cafeBillMakerEmployee, cafeBillPaymentDatetime;
    int cafeBillProductAmount, cafeBillTableNumber;
    HashMap<String, CafeBillDrink> cafeBillDrinks;

    public CafeBill() {}

    public CafeBill(String cafeBillDate, String cafeBillPaymentDatetime, String cafeBillTotalPrice, String cafeBillDelivererEmployee, String cafeBillMakerEmployee,
                    int cafeBillProductAmount, int cafeBillTableNumber, HashMap<String, CafeBillDrink> cafeBillDrinks) {
        this.cafeBillDate = cafeBillDate;
        this.cafeBillPaymentDatetime = cafeBillPaymentDatetime;
        this.cafeBillTotalPrice = cafeBillTotalPrice;
        this.cafeBillDelivererEmployee = cafeBillDelivererEmployee;
        this.cafeBillMakerEmployee = cafeBillMakerEmployee;
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

    public String getCafeBillPaymentDatetime() {
        return cafeBillPaymentDatetime;
    }

    public void setCafeBillPaymentDatetime(String cafeBillPaymentDatetime) {
        this.cafeBillPaymentDatetime = cafeBillPaymentDatetime;
    }

    public String getCafeBillTotalPrice() {
        return cafeBillTotalPrice;
    }

    public void setCafeBillTotalPrice(String cafeBillTotalPrice) {
        this.cafeBillTotalPrice = cafeBillTotalPrice;
    }

    public String getCafeBillDelivererEmployee() {
        return cafeBillDelivererEmployee;
    }

    public void setCafeBillDelivererEmployee(String cafeBillDelivererEmployee) {
        this.cafeBillDelivererEmployee = cafeBillDelivererEmployee;
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
