package com.example.onlinewaiter.ownerUI.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> ownerPhoneNumber = new MutableLiveData<>();
    public LiveData<String> getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }
    public void setOwnerPhoneNumber(String value) {
        ownerPhoneNumber.setValue(value);
    }

    private final MutableLiveData<String> ownerCafeId = new MutableLiveData<>();
    public LiveData<String> getOwnerCafeId() {
        return ownerCafeId;
    }
    public void setOwnerCafeId(String value) {
        ownerCafeId.setValue(value);
    }

    private final MutableLiveData<Integer> updateInfo = new MutableLiveData<>(0);
    public LiveData<Integer> getUpdateInfo() {
        return updateInfo;
    }
    public void setUpdateInfo(Integer value) {
        updateInfo.setValue(value);
    }
}
