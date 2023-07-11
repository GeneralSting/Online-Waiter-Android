package com.example.onlinewaiter.employeeUI.menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CategoryDrink;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuViewModel extends ViewModel {

    private final MutableLiveData<Boolean> displayingCategories = new MutableLiveData<Boolean>();

    public LiveData<Boolean> getDisplayingCategories() {
        return displayingCategories;
    }

    public void setDisplayingCategories(Boolean value) {
        displayingCategories.setValue(value);
    }

    private final MutableLiveData<String> cafeId = new MutableLiveData<String>();

    public LiveData<String> getCafeId() {
        return cafeId;
    }

    public void setCafeId(String value) {
        cafeId.setValue(value);
    }

    private final MutableLiveData<String> phoneNumber = new MutableLiveData<String>();

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String value) {
        phoneNumber.setValue(value);
    }

    private final MutableLiveData<HashMap<String, CategoryDrink>> searchedDrinks = new MutableLiveData<HashMap<String, CategoryDrink>>();

    public LiveData<HashMap<String, CategoryDrink>> getSearchedDrinks() {
        return searchedDrinks;
    }

    public void setSearchedDrinks(HashMap<String, CategoryDrink> value) {
        searchedDrinks.setValue(value);
    }
}