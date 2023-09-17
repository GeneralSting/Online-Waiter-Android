package com.example.onlinewaiter.ownerUI.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatisticsViewModel extends ViewModel {
    private final MutableLiveData<Integer> tableStatisticsReset = new MutableLiveData<>();
    public LiveData<Integer> getTableStatisticsReset() {
        return tableStatisticsReset;
    }
    public void setTableStatisticsReset(Integer value) {
        tableStatisticsReset.setValue(value);
    }
}
