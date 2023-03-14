package com.example.onlinewaiter.Models;

public class DrinksCategory {
    String categoryName, categoryDescription, cateogryImage, categoryId;

    public DrinksCategory() {}

    public DrinksCategory(String categoryName, String categoryDescription, String cateogryImage, String categoryId) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.cateogryImage = cateogryImage;
        this.categoryId = categoryId;
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

    public String getCateogryImage() {
        return cateogryImage;
    }

    public void setCateogryImage(String cateogryImage) {
        this.cateogryImage = cateogryImage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
