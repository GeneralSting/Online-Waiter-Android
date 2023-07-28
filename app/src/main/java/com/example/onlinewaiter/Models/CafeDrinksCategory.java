package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class CafeDrinksCategory {
    private String cafeCategoryDescription, cafeCategoryImage, cafeCategoryName;
    private HashMap<String, CategoryDrink> categoryDrinks;

    public CafeDrinksCategory() {}

    public CafeDrinksCategory(String cafeCategoryDescription, String cafeCategoryImage, String cafeCategoryName, HashMap<String, CategoryDrink> categoryDrinks) {
        this.cafeCategoryDescription = cafeCategoryDescription;
        this.cafeCategoryImage = cafeCategoryImage;
        this.cafeCategoryName = cafeCategoryName;
        this.categoryDrinks = categoryDrinks;
    }

    public String getCafeCategoryDescription() {
        return cafeCategoryDescription;
    }

    public void setCafeCategoryDescription(String cafeCategoryDescription) {
        this.cafeCategoryDescription = cafeCategoryDescription;
    }

    public String getCafeCategoryImage() {
        return cafeCategoryImage;
    }

    public void setCafeCategoryImage(String cafeCategoryImage) {
        this.cafeCategoryImage = cafeCategoryImage;
    }

    public String getCafeCategoryName() {
        return cafeCategoryName;
    }

    public void setCafeCategoryName(String cafeCategoryName) {
        this.cafeCategoryName = cafeCategoryName;
    }

    public HashMap<String, CategoryDrink> getCategoryDrinks() {
        return categoryDrinks;
    }

    public void setCategoryDrinks(HashMap<String, CategoryDrink> categoryDrinks) {
        this.categoryDrinks = categoryDrinks;
    }
}
