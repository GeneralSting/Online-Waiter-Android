package com.example.onlinewaiter.Models;

public class CategoryDrink {
    String categoryDrinkId, categoryDrinkName, categoryDrinkDescription, categoryDrinkImage, categoryDrinkCategoryId;
    float categoryDrinkPrice;

    public CategoryDrink() {}

    public CategoryDrink(String categoryDrinkId, String categoryDrinkName, String categoryDrinkDescription, String categoryDrinkImage, String categoryDrinkCategoryId, float categoryDrinkPrice) {
        this.categoryDrinkId = categoryDrinkId;
        this.categoryDrinkName = categoryDrinkName;
        this.categoryDrinkDescription = categoryDrinkDescription;
        this.categoryDrinkImage = categoryDrinkImage;
        this.categoryDrinkCategoryId = categoryDrinkCategoryId;
        this.categoryDrinkPrice = categoryDrinkPrice;
    }

    public String getCategoryDrinkId() {
        return categoryDrinkId;
    }

    public void setCategoryDrinkId(String categoryDrinkId) {
        this.categoryDrinkId = categoryDrinkId;
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

    public String getCategoryDrinkCategoryId() {
        return categoryDrinkCategoryId;
    }

    public void setCategoryDrinkCategoryId(String categoryDrinkCategoryId) {
        this.categoryDrinkCategoryId = categoryDrinkCategoryId;
    }

    public float getCategoryDrinkPrice() {
        return categoryDrinkPrice;
    }

    public void setCategoryDrinkPrice(float categoryDrinkPrice) {
        this.categoryDrinkPrice = categoryDrinkPrice;
    }
}
