package com.example.onlinewaiter.ownerUI.statistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.onlinewaiter.Formater.DecimalRemover;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.CafeBill;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentStatisticsBinding;
import com.example.onlinewaiter.ownerUI.main.MainViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment {

    //global variables/objects
    private FragmentStatisticsBinding binding;
    MainViewModel mainViewModel;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
    private AppError appError;
    HashMap<String, Integer> pieChartStatistic;
    int databaseQuerySize = 100;

    //fragment views
    Button btnStatisticsEmployees, btnStatisticsDrinks, btnStatisticsTables;
    PieChart pieChart;
    PieData pieData;
    TableLayout tlStatisticEmployees, tlStatisticEmployeesTitle;

    //firebase
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference cafeBillsRef;
    FirebaseRefPaths firebaseRefPaths;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseRefPaths = new FirebaseRefPaths();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        pieChartStatistic = new HashMap<>();

        btnStatisticsEmployees = binding.btnStatisticsEmployees;
        btnEmploeesCLickEvent();

        btnStatisticsDrinks = binding.btnStatisticsDrinks;
        btnDrinkClickEvent();

        btnStatisticsTables = binding.btnStatisticsTables;
        btnTablesClickEvent();

        return root;
    }
    private void btnEmploeesCLickEvent() {
        btnStatisticsEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View statisticsView = getLayoutInflater().inflate(R.layout.dialog_statistics_chart, null);
                TextView tvDialogStatisticsTitle = (TextView) statisticsView.findViewById(R.id.tvDialogStatisticsTitle);
                tvDialogStatisticsTitle.setText(getResources().getString(R.string.statistics_btn_employees));
                tvDialogStatisticsTitle.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_blue_overlay));

                Button btnDialogStatisticsNumbers = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsNumbers);
                Button btnDialogStatisticsPercentages = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsPercentages);
                Button btnDialogStatisticsTable = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsTable);
                Button btnDialogStatisticsSize = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsSize);

                EditText etStatisticQuerySize = (EditText) statisticsView.findViewById(R.id.etStatisticQuerySize);
                ImageView ivStatisticQuerySize = (ImageView) statisticsView.findViewById(R.id.ivStatisticQuerySize);

                tlStatisticEmployees = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployees);
                tlStatisticEmployeesTitle = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployeesTitle);

                pieChart = (PieChart) statisticsView.findViewById(R.id.pcDialogStatistics);
                List<PieEntry> pieEntryList = new ArrayList<>();

                final AlertDialog statisticsDialog = new AlertDialog.Builder(getActivity())
                        .setView(statisticsView)
                        .create();
                statisticsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        databaseQuery(databaseQuerySize);

                        btnDialogStatisticsNumbers.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));

                                tlStatisticEmployeesTitle.setVisibility(View.GONE);
                                pieChart.setVisibility(View.VISIBLE);
                                pieChart.setUsePercentValues(false);
                                pieData.setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value) {
                                        return String.valueOf((int) Math.floor(value));
                                    }
                                });
                                pieChart.invalidate();
                            }
                        });

                        btnDialogStatisticsPercentages.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));

                                tlStatisticEmployeesTitle.setVisibility(View.GONE);
                                pieChart.setVisibility(View.VISIBLE);
                                pieChart.setUsePercentValues(true);
                                pieData.setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value) {
                                        return String.valueOf((int) Math.floor(value)) + "%";
                                    }
                                });
                                pieChart.invalidate();
                            }
                        });

                        btnDialogStatisticsTable.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));

                                pieChart.setVisibility(View.GONE);
                                tlStatisticEmployeesTitle.setVisibility(View.VISIBLE);
                                populateEmployeesTable();
                            }
                        });
                    }
                });
                statisticsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        pieChartStatistic = new HashMap<>();
                    }
                });
                statisticsDialog.show();
            }
        });
    }

    private void btnDrinkClickEvent() {
        btnStatisticsDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void databaseQuery(int querySize) {
        cafeBillsRef = firebaseDatabase.getReference(firebaseRefPaths.getOwnerRefCafeBills(mainViewModel.getOwnerCafeId().getValue()));
        Query queryCafeBills = cafeBillsRef.limitToLast(querySize);
        queryCafeBills.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeBillsSnapshot) {
                if(!cafeBillsSnapshot.exists()) {
                    return;
                }
                boolean pieChartEntriesFull = false;
                for(DataSnapshot cafeBillSnapshot : cafeBillsSnapshot.getChildren()) {
                    if(!cafeBillSnapshot.exists()) {
                        return;
                    }
                    if(pieChartStatistic.size() > 9) {
                        pieChartEntriesFull = true;
                    }
                    CafeBill cafeBill = cafeBillSnapshot.getValue(CafeBill.class);
                    if(pieChartStatistic == null || pieChartStatistic.isEmpty()) {
                        pieChartStatistic.put(cafeBill.getCafeBillDelivererEmployee(), 1);
                    }
                    else {
                        if(pieChartStatistic.containsKey(cafeBill.getCafeBillDelivererEmployee())) {
                            pieChartStatistic.put(cafeBill.getCafeBillDelivererEmployee(), pieChartStatistic.get(
                                    cafeBill.getCafeBillDelivererEmployee()) + 1);
                        }
                        else if(!pieChartEntriesFull) {
                            pieChartStatistic.put(cafeBill.getCafeBillDelivererEmployee(), 1);
                        }
                    }
                }
                makePieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void btnTablesClickEvent() {
        btnStatisticsTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void makePieChart() {
        if(pieChartStatistic == null || pieChartStatistic.isEmpty()) {
            return;
        }

        List<PieEntry> pieEntryList  = new ArrayList<>();
        for(String key : pieChartStatistic.keySet()) {
            pieEntryList.add(new PieEntry(pieChartStatistic.get(key), key));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, AppConstValue.statisticsConstValues.PIE_CHART_LABEL);
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieData.setValueTextSize(12f);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setText(getResources().getString(R.string.statistics_pie_chart_description));
        pieChart.getDescription().setTextSize(12f);
        pieChart.getDescription().setTextColor(getResources().getColor(R.color.white));
        pieChart.setExtraBottomOffset(2f);
        pieChart.setHoleColor(getResources().getColor(R.color.cv_cafe_update_blue_overlay));
        pieChart.invalidate();
    }

    private void populateEmployeesTable() {
        while(tlStatisticEmployees.getChildCount() > 0) {
            tlStatisticEmployees.removeView(tlStatisticEmployees.getChildAt(tlStatisticEmployees.getChildCount() - 1));
        }

        for(String key : pieChartStatistic.keySet()) {
            View trEmployeeView = getLayoutInflater().inflate(R.layout.statistic_employee_row, tlStatisticEmployees, false);

            TextView tvStatisticEmployeePhoneNumber = (TextView) trEmployeeView.findViewById(R.id.tvStatisticEmployeePhoneNumber);
            TextView tvStatisticEmployeeValue = (TextView) trEmployeeView.findViewById(R.id.tvStatisticEmployeeValue);
            TextView tvStatisticEmployeePercent = (TextView) trEmployeeView.findViewById(R.id.tvStatisticEmployeePercent);

            tvStatisticEmployeePhoneNumber.setText(key);
            tvStatisticEmployeeValue.setText(String.valueOf(pieChartStatistic.get(key)));
            tvStatisticEmployeePercent.setText("pavo");
            tlStatisticEmployees.addView(trEmployeeView);
        }

    }
}