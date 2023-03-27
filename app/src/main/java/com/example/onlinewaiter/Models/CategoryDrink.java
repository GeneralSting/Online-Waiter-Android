package com.example.onlinewaiter.Models;

public class CategoryDrink {
    String categoryDrinkName, categoryDrinkDescription, categoryDrinkImage;
    float categoryDrinkPrice;

    public CategoryDrink() {}

    public CategoryDrink(String categoryDrinkDescription, String categoryDrinkImage, String categoryDrinkName, float categoryDrinkPrice) {
        this.categoryDrinkDescription = categoryDrinkDescription;
        this.categoryDrinkImage = categoryDrinkImage;
        this.categoryDrinkName = categoryDrinkName;
        this.categoryDrinkPrice = categoryDrinkPrice;
    }

    public String getCategoryDrinkName() {
        return categoryDrinkName;
    }

    public void setCategoryDrinkName(String categoryDrinkName) {
        this.categoryDrinkName = categoryDrinkName;
    }

    public String getCategoryDrinkDescription() {
        return categoryDrinkDescription;
    }

    public void setCategoryDrinkDescription(String categoryDrinkDescription) {
        this.categoryDrinkDescription = categoryDrinkDescription;
    }

    public String getCategoryDrinkImage() {
        return categoryDrinkImage;
    }

    public void setCategoryDrinkImage(String categoryDrinkImage) {
        this.categoryDrinkImage = categoryDrinkImage;
    }


    public float getCategoryDrinkPrice() {
        return categoryDrinkPrice;
    }

    public void setCategoryDrinkPrice(float categoryDrinkPrice) {
        this.categoryDrinkPrice = categoryDrinkPrice;
    }
}
