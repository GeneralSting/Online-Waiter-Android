package com.example.onlinewaiter.employeeUI.cafeUpdate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CafeUpdateViewModel extends ViewModel {

    private final MutableLiveData<Integer> cafeTables = new MutableLiveData<Integer>();

    public LiveData<Integer> getCafeTables() {
        return cafeTables;
    }

    public void setCafeTables(Integer value) {
        cafeTables.setValue(value);
    }
}