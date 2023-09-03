package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class CafeBill {
    private String cafeBillDate, cafeBillTotalPrice, cafeBillDelivererNum, cafeBillDelivererEmployee, cafeBillMakerNum, cafeBillMakerEmployee, cafeBillPaymentDatetime;
    private int cafeBillProductAmount, cafeBillTableNumber;
    private HashMap<String, CafeBillDrink> cafeBillDrinks;

    public CafeBill() {}

    public CafeBill(String cafeBillDate, String cafeBillPaymentDatetime, String cafeBillTotalPrice, String cafeBillDelivererNum, String cafeBillDelivererEmployee,
                    String cafeBillMakerNum, String cafeBillMakerEmployee, int cafeBillProductAmount, int cafeBillTableNumber, HashMap<String, CafeBillDrink> cafeBillDrinks) {
        this.cafeBillDate = cafeBillDate;
        this.cafeBillPaymentDatetime = cafeBillPaymentDatetime;
        this.cafeBillTotalPrice = cafeBillTotalPrice;
        this.cafeBillDelivererNum = cafeBillDelivererNum;
        this.cafeBillDelivererEmployee = cafeBillDelivererEmployee;
        this.cafeBillMakerNum = cafeBillMakerNum;
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

    public String getCafeBillMakerEmployee() {
        return cafeBillMakerEmployee;
    }

    public void setCafeBillMakerEmployee(String cafeBillMakerEmployee) {
        this.cafeBillMakerEmployee = cafeBillMakerEmployee;
    }

    public String getCafeBillDelivererNum() {
        return cafeBillDelivererNum;
    }

    public void setCafeBillDelivererNum(String cafeBillDelivererNum) {
        this.cafeBillDelivererNum = cafeBillDelivererNum;
    }

    public String getCafeBillMakerNum() {
        return cafeBillMakerNum;
    }

    public void setCafeBillMakerNum(String cafeBillMakerNum) {
        this.cafeBillMakerNum = cafeBillMakerNum;
    }
}
