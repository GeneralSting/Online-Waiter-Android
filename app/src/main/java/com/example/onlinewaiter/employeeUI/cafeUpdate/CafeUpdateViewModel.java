package com.example.onlinewaiter.employeeUI.cafeUpdate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CafeUpdateViewModel extends ViewModel {
    private final MutableLiveData<Integer> cafeUpdateRvDisplayed = new MutableLiveData<>(-1);
    public LiveData<Integer> getCafeUpdateRvDispalyed() {
        return cafeUpdateRvDisplayed;
    }
    public void setCafeUpdateRvDisplayed(Integer value) {
        cafeUpdateRvDisplayed.setValue(value);
    }

    private final MutableLiveData<Boolean> cafeUpdateDisplayChange = new MutableLiveData<>(false);
    public LiveData<Boolean> getCafeUpdateDisplayChange() {
        return cafeUpdateDisplayChange;
    }
    public void setCafeUpdateDisplayChange(Boolean value) {
        cafeUpdateDisplayChange.setValue(value);
    }
}