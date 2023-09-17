package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class CafeCurrentOrder {
    private String currentOrderDatetime, currentOrderTotalPrice, currentOrderDelivererNum, currentOrderDelivererEmployee,
            currentOrderMakerNum, currentOrderMakerEmployee, currentOrderMessage;
    private int currentOrderProductAmount, currentOrderTableNumber, currentOrderStatus, currentOrderTablesStatistic;
    private HashMap<String, CafeBillDrink> currentOrderDrinks;

    public CafeCurrentOrder() {}

    public CafeCurrentOrder(String currentOrderDatetime, String currentOrderTotalPrice, String currentOrderDelivererNum, String currentOrderDelivererEmployee,
                            String currentOrderMakerNum, String currentOrderMakerEmployee, int currentOrderProductAmount, int currentOrderTableNumber,
                            HashMap<String, CafeBillDrink> currentOrderDrinks, int currentOrderStatus, int currentOrderTablesStatistic) {
        this.currentOrderDatetime = currentOrderDatetime;
        this.currentOrderTotalPrice = currentOrderTotalPrice;
        this.currentOrderDelivererNum = currentOrderDelivererNum;
        this.currentOrderDelivererEmployee = currentOrderDelivererEmployee;
        this.currentOrderMakerNum = currentOrderMakerNum;
        this.currentOrderMakerEmployee = currentOrderMakerEmployee;
        this.currentOrderProductAmount = currentOrderProductAmount;
        this.currentOrderTableNumber = currentOrderTableNumber;
        this.currentOrderDrinks = currentOrderDrinks;
        this.currentOrderStatus = currentOrderStatus;
        this.currentOrderTablesStatistic = currentOrderTablesStatistic;
    }

    public CafeCurrentOrder(String currentOrderDatetime, String currentOrderTotalPrice, String currentOrderDelivererNum, String currentOrderDelivererEmployee,
                            String currentOrderMakerNum, String currentOrderMakerEmployee, String currentOrderMessage, int currentOrderProductAmount,
                            int currentOrderTableNumber, HashMap<String, CafeBillDrink> currentOrderDrinks, int currentOrderStatus,int currentOrderTablesStatistic) {
        this.currentOrderDatetime = currentOrderDatetime;
        this.currentOrderTotalPrice = currentOrderTotalPrice;
        this.currentOrderDelivererNum = currentOrderDelivererNum;
        this.currentOrderDelivererEmployee = currentOrderDelivererEmployee;
        this.currentOrderMakerNum = currentOrderMakerNum;
        this.currentOrderMakerEmployee = currentOrderMakerEmployee;
        this.currentOrderMessage = currentOrderMessage;
        this.currentOrderProductAmount = currentOrderProductAmount;
        this.currentOrderTableNumber = currentOrderTableNumber;
        this.currentOrderDrinks = currentOrderDrinks;
        this.currentOrderStatus = currentOrderStatus;
        this.currentOrderTablesStatistic = currentOrderTablesStatistic;
    }

    public CafeCurrentOrder(String currentOrderDatetime, String currentOrderTotalPrice, String currentOrderDelivererNum, String currentOrderDelivererEmployee,
                            int currentOrderProductAmount, int currentOrderTableNumber, HashMap<String, CafeBillDrink> currentOrderDrinks,
                            int currentOrderStatus, int currentOrderTablesStatistic) {
        this.currentOrderDatetime = currentOrderDatetime;
        this.currentOrderTotalPrice = currentOrderTotalPrice;
        this.currentOrderDelivererNum = currentOrderDelivererNum;
        this.currentOrderDelivererEmployee = currentOrderDelivererEmployee;
        this.currentOrderProductAmount = currentOrderProductAmount;
        this.currentOrderTableNumber = currentOrderTableNumber;
        this.currentOrderDrinks = currentOrderDrinks;
        this.currentOrderStatus = currentOrderStatus;
        this.currentOrderTablesStatistic = currentOrderTablesStatistic;
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

    public int getCurrentOrderStatus() {
        return currentOrderStatus;
    }

    public void setCurrentOrderStatus(int currentOrderStatus) {
        this.currentOrderStatus = currentOrderStatus;
    }

    public String getCurrentOrderMessage() {
        return currentOrderMessage;
    }

    public void setCurrentOrderMessage(String cafeCurrentOrderMessage) {
        this.currentOrderMessage = cafeCurrentOrderMessage;
    }

    public String getCurrentOrderDelivererNum() {
        return currentOrderDelivererNum;
    }

    public void setCurrentOrderDelivererNum(String currentOrderDelivererNum) {
        this.currentOrderDelivererNum = currentOrderDelivererNum;
    }

    public String getCurrentOrderDelivererEmployee() {
        return currentOrderDelivererEmployee;
    }

    public void setCurrentOrderDelivererEmployee(String currentOrderDelivererEmployee) {
        this.currentOrderDelivererEmployee = currentOrderDelivererEmployee;
    }

    public String getCurrentOrderMakerNum() {
        return currentOrderMakerNum;
    }

    public void setCurrentOrderMakerNum(String currentOrderMakerNum) {
        this.currentOrderMakerNum = currentOrderMakerNum;
    }

    public int getCurrentOrderTablesStatistic() {
        return currentOrderTablesStatistic;
    }

    public void setCurrentOrderTablesStatistic(int currentOrderTablesStatistic) {
        this.currentOrderTablesStatistic = currentOrderTablesStatistic;
    }
}
