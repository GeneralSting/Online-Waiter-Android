package com.example.onlinewaiter.ownerUI.registeredNumbers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisteredNumbersViewModel extends ViewModel {
    private final MutableLiveData<Integer> emailRandomCode = new MutableLiveData<>(1000000);
    public LiveData<Integer> getEmailRandomCode() {
        return emailRandomCode;
    }
    public void setEmailRandomCode(Integer value) {
        emailRandomCode.setValue(value);
    }

    private final MutableLiveData<Boolean> phoneNumberChanged = new MutableLiveData<>(false);
    public LiveData<Boolean> getPhoneNumberChanged() {
        return phoneNumberChanged;
    }
    public void setPhoneNumberChanged(Boolean value) {
        phoneNumberChanged.setValue(value);
    }
}

