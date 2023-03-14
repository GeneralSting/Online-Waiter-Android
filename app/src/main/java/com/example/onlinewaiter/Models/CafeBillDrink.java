package com.example.onlinewaiter.Models;

public class CafeBillDrink {
    String drinkId, drinkName, drinkImage;
    float drinkPrice, drinkTotalPrice;
    int drinkAmount;

    public CafeBillDrink() {}

    public CafeBillDrink(String drinkId, String drinkName, String drinkImage, float drinkPrice, float drinkTotalPrice, int drinkAmount) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.drinkImage = drinkImage;
        this.drinkPrice = drinkPrice;
        this.drinkTotalPrice = drinkTotalPrice;
        this.drinkAmount = drinkAmount;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public String getDrinkImage() {
        return drinkImage;
    }

    public void setDrinkImage(String drinkImage) {
        this.drinkImage = drinkImage;
    }

    public float getDrinkPrice() {
        return drinkPrice;
    }

    public void setDrinkPrice(float drinkPrice) {
        this.drinkPrice = drinkPrice;
    }

    public float getDrinkTotalPrice() {
        return drinkTotalPrice;
    }

    public void setDrinkTotalPrice(float drinkTotalPrice) {
        this.drinkTotalPrice = drinkTotalPrice;
    }

    public int getDrinkAmount() {
        return drinkAmount;
    }

    public void setDrinkAmount(int drinkAmount) {
        this.drinkAmount = drinkAmount;
    }
}
