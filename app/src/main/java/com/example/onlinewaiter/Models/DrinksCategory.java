package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class DrinksCategory {
    private String categoryName, categoryDescription, categoryImage;
    private HashMap<String, String> categoryNames;

    public DrinksCategory() {}

    public DrinksCategory(String categoryName, String categoryDescription, String categoryImage) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.categoryImage = categoryImage;
    }

    public DrinksCategory(HashMap<String, String> categoryNames, String categoryDescription, String categoryImage) {
        this.categoryNames = categoryNames;
        this.categoryDescription = categoryDescription;
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public HashMap<String, String> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(HashMap<String, String> categoryNames) {
        this.categoryNames = categoryNames;
    }
}
