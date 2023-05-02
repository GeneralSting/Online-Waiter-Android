package com.example.onlinewaiter.Formater;

import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.Map;

public class DecimalRemover extends PercentFormatter {

    protected DecimalFormat mFormat;

    public DecimalRemover(DecimalFormat format) {
        this.mFormat = format;
    }

    public String getFormattedValue(float value, Map.Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value) + " %";
    }
}
