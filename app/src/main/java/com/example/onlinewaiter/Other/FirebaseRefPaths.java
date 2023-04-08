package com.example.onlinewaiter.Other;

public class FirebaseRefPaths {
    String refPathCafes = "cafes/";
    String refPathCafeDrinksCategories = "/cafeDrinksCategories/";
    String refPathCategoryDrinks = "/categoryDrinks/";
    String refPathCafesBills = "/cafeBills/";
    String refPathCafeCurrentOrders = "/cafeCurrentOrders/";
    String refAppErrors = "appErrors/";

    public FirebaseRefPaths() {

    }

    public String getRefPathCafes() {
        return refPathCafes;
    }

    public String getRefPathCafeDrinksCategories() {
        return refPathCafeDrinksCategories;
    }

    public String getRefPathCategoryDrinks() {
        return refPathCategoryDrinks;
    }

    public String getRefPathCafesBills() {
        return refPathCafesBills;
    }

    public String getRefPathCafeCurrentOrders() {
        return refPathCafeCurrentOrders;
    }

    public String getRefAppErrors() {
        return refAppErrors;
    }
}
