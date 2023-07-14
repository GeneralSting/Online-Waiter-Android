package com.example.onlinewaiter.ownerUI.statistics;

import android.content.Context;

import com.example.onlinewaiter.R;

public class StatisticsPieChartColors {
    int[] pieChartColors;

    public StatisticsPieChartColors(Context context) {
        pieChartColors = new int[]{
                context.getColor(R.color.pie_chart_darker_green),
                context.getColor(R.color.pie_chart_lighter_green),
                context.getColor(R.color.pie_chart_darker_red),
                context.getColor(R.color.pie_chart_lighter_red),
                context.getColor(R.color.pie_chart_darker_orange),
                context.getColor(R.color.pie_chart_lighter_orange),
                context.getColor(R.color.pie_chart_darker_purple),
                context.getColor(R.color.pie_chart_lighter_purple),
                context.getColor(R.color.pie_chart_lighter_blue),
                context.getColor(R.color.pie_chart_darker_blue),
        };
    }

    public int[] getPieChartColors() {
        return pieChartColors;
    }
}
