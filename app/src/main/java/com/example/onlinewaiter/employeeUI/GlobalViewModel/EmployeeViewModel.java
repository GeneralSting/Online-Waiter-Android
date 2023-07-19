package com.example.onlinewaiter.employeeUI.GlobalViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;

public class EmployeeViewModel extends ViewModel {
    private final MutableLiveData<String> cafeCurrency = new MutableLiveData<>();

    public LiveData<String> getCafeCurrency() {
        return cafeCurrency;
    }

    public void setCafeCurrency(String value) {
        cafeCurrency.setValue(value);
    }

    private final MutableLiveData<String> cafeDateTimeFormat = new MutableLiveData<>();

    public LiveData<String> getCafeDateTimeFormat() {
        return cafeDateTimeFormat;
    }

    public void setCafeDateTimeFormat(String value) {
        cafeDateTimeFormat.setValue(value);
    }

    private final MutableLiveData<String> cafeDecimalSeperator = new MutableLiveData<>();

    public LiveData<String> getCafeDecimalSeperator() {
        return cafeDecimalSeperator;
    }

    public void setCafeDecimalSeperator(String value) {
        cafeDecimalSeperator.setValue(value);
    }
}
