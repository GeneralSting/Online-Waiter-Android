package com.example.onlinewaiter.employeeUI.pendingOrders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PendingOrdersViewModel extends ViewModel {
    private final MutableLiveData<Integer> searchedOrder = new MutableLiveData<Integer>(0);

    public LiveData<Integer> getSearchedOrder() {
        return searchedOrder;
    }

    public void setSearchedOrder(Integer value) {
        searchedOrder.setValue(value);
    }
}
