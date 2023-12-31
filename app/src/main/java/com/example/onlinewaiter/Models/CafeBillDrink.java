package com.example.onlinewaiter.Models;

public class CafeBillDrink {
    private String drinkId, drinkName, drinkImage, categoryId;
    private float drinkPrice, drinkTotalPrice;
    private int drinkAmount, drinkQuantity;

    public CafeBillDrink() {}

    public CafeBillDrink(String drinkId, String drinkName, String drinkImage, float drinkPrice, float drinkTotalPrice, int drinkAmount, int drinkQuantity) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.drinkImage = drinkImage;
        this.drinkPrice = drinkPrice;
        this.drinkTotalPrice = drinkTotalPrice;
        this.drinkAmount = drinkAmount;
        this.drinkQuantity = drinkQuantity;
    }

    public CafeBillDrink(String drinkId, String categoryId, String drinkName, String drinkImage, float drinkPrice, float drinkTotalPrice, int drinkAmount, int drinkQuantity) {
        this.drinkId = drinkId;
        this.categoryId = categoryId;
        this.drinkName = drinkName;
        this.drinkImage = drinkImage;
        this.drinkPrice = drinkPrice;
        this.drinkTotalPrice = drinkTotalPrice;
        this.drinkAmount = drinkAmount;
        this.drinkQuantity = drinkQuantity;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getDrinkQuantity() {
        return drinkQuantity;
    }

    public void setDrinkQuantity(int drinkQuantity) {
        this.drinkQuantity = drinkQuantity;
    }
}
