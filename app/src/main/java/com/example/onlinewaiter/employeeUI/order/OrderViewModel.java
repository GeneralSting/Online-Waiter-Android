package com.example.onlinewaiter.employeeUI.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinewaiter.Models.CafeBillDrink;

import java.util.HashMap;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<HashMap<String, CafeBillDrink>> drinksInOrder = new MutableLiveData<HashMap<String, CafeBillDrink>>();

    public LiveData<HashMap<String, CafeBillDrink>> getDrinksInOrder() {
        return drinksInOrder;
    }

    public void setDrinksInOrder(HashMap<String, CafeBillDrink> value) {
        drinksInOrder.setValue(value);
    }

    private final MutableLiveData<Integer> cafeTablesNumber = new MutableLiveData<Integer>();

    public LiveData<Integer> getCafeTablesNumber() {
        return cafeTablesNumber;
    }

    public void setCafeTablesNumber(Integer value) {
        cafeTablesNumber.setValue(value);
    }

    private final MutableLiveData<Integer> checkDrinksOrder = new MutableLiveData<>();
    public LiveData<Integer> getCheckDrinksOrder() {
        return checkDrinksOrder;
    }
    public void setCheckDrinksOrder(Integer value) {
        checkDrinksOrder.setValue(value);
    }

}