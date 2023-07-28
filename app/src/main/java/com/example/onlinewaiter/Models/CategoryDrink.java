package com.example.onlinewaiter.Models;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.R;

public class CategoryDrink {
    private String categoryId, categoryDrinkName, categoryDrinkDescription, categoryDrinkImage;
    private float categoryDrinkPrice;
    private int categoryDrinkQuantity;

    public CategoryDrink() {}

    public CategoryDrink(String categoryId, String categoryDrinkDescription, String categoryDrinkImage, String categoryDrinkName, float categoryDrinkPrice, int categoryDrinkQuantity) {
        this.categoryId = categoryId;
        this.categoryDrinkDescription = categoryDrinkDescription;
        this.categoryDrinkImage = categoryDrinkImage;
        this.categoryDrinkName = categoryDrinkName;
        this.categoryDrinkPrice = categoryDrinkPrice;
        this.categoryDrinkQuantity = categoryDrinkQuantity;
    }

    public CategoryDrink(String categoryDrinkDescription, String categoryDrinkImage, String categoryDrinkName, float categoryDrinkPrice, int categoryDrinkQuantity) {
        this.categoryDrinkDescription = categoryDrinkDescription;
        this.categoryDrinkImage = categoryDrinkImage;
        this.categoryDrinkName = categoryDrinkName;
        this.categoryDrinkPrice = categoryDrinkPrice;
        this.categoryDrinkQuantity = categoryDrinkQuantity;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public int getCategoryDrinkQuantity() {
        return categoryDrinkQuantity;
    }

    public void setCategoryDrinkQuantity(int categoryDrinkQuantity) {
        this.categoryDrinkQuantity = categoryDrinkQuantity;
    }

    public String getShortenQunatity() {
        if(this.categoryDrinkQuantity > 99) {
            return AppConstValue.variableConstValue.DRINK_QUANTITY_OVER_HUNDRED;
        }
        return String.valueOf(categoryDrinkQuantity);
    }

    public int getAvailabilityWarning(Context context) {
        if(this.categoryDrinkQuantity == 0) {
            return context.getResources().getColor(R.color.red_negative);
        }
        if(this.categoryDrinkQuantity < 10) {
            return context.getResources().getColor(R.color.pie_chart_lighter_orange);
        }
        return context.getResources().getColor(R.color.white);
    }
}
