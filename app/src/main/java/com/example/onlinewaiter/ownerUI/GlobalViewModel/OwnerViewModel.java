package com.example.onlinewaiter.ownerUI.GlobalViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinewaiter.Models.RegisteredCountry;

public class OwnerViewModel extends ViewModel {
    private final MutableLiveData<RegisteredCountry> cafeCountryStandards = new MutableLiveData<>();
    public LiveData<RegisteredCountry> getCafeCountryStandards() {
        return cafeCountryStandards;
    }
    public void setCafeCountryStandards(RegisteredCountry value) {
        cafeCountryStandards.setValue(value);
    }
}
