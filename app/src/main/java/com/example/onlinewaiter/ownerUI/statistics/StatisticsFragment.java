package com.example.onlinewaiter.ownerUI.statistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.CafeBill;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentStatisticsBinding;
import com.example.onlinewaiter.ownerUI.GlobalViewModel.OwnerViewModel;
import com.example.onlinewaiter.ownerUI.main.MainViewModel;
import com.github.mikephil.charting.charts.PieChart;
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
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class StatisticsFragment extends Fragment {
    //fragment views
    private Button btnStatisticsEmployees, btnStatisticsDrinks, btnStatisticsTables;
    private PieChart pieChart;
    private PieData pieData;
    private TableLayout tlStatisticEmployees, tlStatisticEmployeesTitle;
    private TextView tvStatisticsSize;

    //global variables/objects
    private FragmentStatisticsBinding binding;
    final int databaseQuerySize = AppConstValue.variableConstValue.STATISTICS_DEFAULT_QUERY_SIZE;
    private boolean showChart;
    private HashMap<String, Integer> pieChartStatistic;
    private ToastMessage toastMessage;
    private MainViewModel mainViewModel;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private DecimalFormat cafeDecimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO);
    private DecimalFormatSymbols decimalFormatSymbols;
    private AppError appError;
    private StatisticsPieChartColors statisticsPieChartColors;

    //firebase
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseRefPaths firebaseRefPaths;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnStatisticsEmployees = binding.btnStatisticsEmployees;
        btnStatisticsDrinks = binding.btnStatisticsDrinks;
        btnStatisticsTables = binding.btnStatisticsTables;

        firebaseRefPaths = new FirebaseRefPaths();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        pieChartStatistic = new HashMap<>();
        toastMessage = new ToastMessage(requireActivity());
        OwnerViewModel ownerViewModel = new ViewModelProvider(requireActivity()).get(OwnerViewModel.class);
        decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(Objects.requireNonNull(ownerViewModel.getCafeCountryStandards().getValue().getDecimalSeperator()).charAt(0));
        cafeDecimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO, decimalFormatSymbols);
        statisticsPieChartColors = new StatisticsPieChartColors(requireActivity());

        btnEmploeesCLickEvent();
        btnDrinkClickEvent();
        btnTablesClickEvent();

        return root;
    }
    private void btnEmploeesCLickEvent() {
        btnStatisticsEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChart = true;
                View statisticsView = getLayoutInflater().inflate(R.layout.dialog_statistics_chart, null);
                LinearLayoutCompat llStatisticsContentContainer = (LinearLayoutCompat) statisticsView.findViewById(R.id.llStatisticsContentContainer);
                int containerPL = llStatisticsContentContainer.getPaddingLeft();
                int containerPT = llStatisticsContentContainer.getPaddingTop();
                int containerPR = llStatisticsContentContainer.getPaddingRight();
                int containerPB = llStatisticsContentContainer.getPaddingBottom();
                llStatisticsContentContainer.setBackgroundResource(R.drawable.action_dialog_owner_blue);
                llStatisticsContentContainer.setPadding(containerPL, containerPT, containerPR, containerPB);

                ImageView ivOwnerStatisticsIcon = (ImageView) statisticsView.findViewById(R.id.ivOwnerStatisticsIcon);
                int iconPL = ivOwnerStatisticsIcon.getPaddingLeft();
                int iconPT = ivOwnerStatisticsIcon.getPaddingTop();
                int iconPR = ivOwnerStatisticsIcon.getPaddingRight();
                int iconPB = ivOwnerStatisticsIcon.getPaddingBottom();
                ivOwnerStatisticsIcon.setBackgroundResource(R.drawable.action_dialog_owner_blue);
                ivOwnerStatisticsIcon.setPadding(iconPL, iconPT, iconPR, iconPB);

                TableRow trOwnerStatisticEmployeeTitle = (TableRow) statisticsView.findViewById(R.id.trOwnerStatisticEmployeeTitle);
                trOwnerStatisticEmployeeTitle.setBackgroundColor(getResources().getColor(R.color.dialog_action_owner_blue));
                TextView tvDialogStatisticsTitle = (TextView) statisticsView.findViewById(R.id.tvDialogStatisticsTitle);
                tvStatisticsSize = (TextView) statisticsView.findViewById(R.id.tvStatisticsSize);
                tvDialogStatisticsTitle.setText(getResources().getString(R.string.statistics_title_employees));

                Button btnDialogStatisticsNumbers = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsNumbers);
                Button btnDialogStatisticsPercentages = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsPercentages);
                Button btnDialogStatisticsTable = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsTable);
                Button btnDialogStatisticsSize = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsSize);

                EditText etStatisticQuerySize = (EditText) statisticsView.findViewById(R.id.etStatisticQuerySize);
                ImageView ivStatisticQuerySize = (ImageView) statisticsView.findViewById(R.id.ivStatisticQuerySize);

                tlStatisticEmployees = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployees);
                tlStatisticEmployeesTitle = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployeesTitle);

                pieChart = (PieChart) statisticsView.findViewById(R.id.pcDialogStatistics);
                pieChart.getDescription().setText(getResources().getString(R.string.statistics_employees_chart_description));
                List<PieEntry> pieEntryList = new ArrayList<>();

                final AlertDialog statisticsDialog = new AlertDialog.Builder(getActivity())
                        .setView(statisticsView)
                        .create();
                statisticsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                statisticsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        databaseQuery(databaseQuerySize, 0);
                        btnDialogStatisticsNumbers.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = true;
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
                                makePieChart(false);
                            }
                        });

                        btnDialogStatisticsPercentages.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = true;
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));

                                tlStatisticEmployeesTitle.setVisibility(View.GONE);
                                pieChart.setVisibility(View.VISIBLE);

                                makePieChart(true);
                            }
                        });

                        btnDialogStatisticsTable.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = false;
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));

                                pieChart.setVisibility(View.GONE);
                                tlStatisticEmployeesTitle.setVisibility(View.VISIBLE);
                                populateEmployeesTable();
                            }
                        });

                        btnDialogStatisticsSize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(btnDialogStatisticsSize.getBackgroundTintList() == getResources().getColorStateList(R.color.light_tumbleweed)) {
                                    btnDialogStatisticsSize.setBackgroundTintList(getResources().getColorStateList(R.color.tumbleweed));
                                    etStatisticQuerySize.setVisibility(View.GONE);
                                    etStatisticQuerySize.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                                    ivStatisticQuerySize.setVisibility(View.GONE);
                                }
                                else {
                                    btnDialogStatisticsSize.setBackgroundTintList(getResources().getColorStateList(R.color.light_tumbleweed));
                                    etStatisticQuerySize.setVisibility(View.VISIBLE);
                                    ivStatisticQuerySize.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        ivStatisticQuerySize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(etStatisticQuerySize.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                    toastMessage.showToast(getResources().getString(R.string.statistics_query_size_failed), 0);
                                }
                                else if(Integer.parseInt(etStatisticQuerySize.getText().toString()) <= 0) {
                                    toastMessage.showToast(getResources().getString(R.string.statistics_query_size_failed), 0);
                                }
                                else {
                                    pieChartStatistic = new HashMap<>();
                                    databaseQuery(Integer.parseInt(etStatisticQuerySize.getText().toString()), 0);
                                }
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
                showChart = true;
                View statisticsView = getLayoutInflater().inflate(R.layout.dialog_statistics_chart, null);
                LinearLayoutCompat llStatisticsContentContainer = (LinearLayoutCompat) statisticsView.findViewById(R.id.llStatisticsContentContainer);
                int containerPL = llStatisticsContentContainer.getPaddingLeft();
                int containerPT = llStatisticsContentContainer.getPaddingTop();
                int containerPR = llStatisticsContentContainer.getPaddingRight();
                int containerPB = llStatisticsContentContainer.getPaddingBottom();
                llStatisticsContentContainer.setBackgroundResource(R.drawable.action_dialog_owner_green);
                llStatisticsContentContainer.setPadding(containerPL, containerPT, containerPR, containerPB);

                ImageView ivOwnerStatisticsIcon = (ImageView) statisticsView.findViewById(R.id.ivOwnerStatisticsIcon);
                int iconPL = ivOwnerStatisticsIcon.getPaddingLeft();
                int iconPT = ivOwnerStatisticsIcon.getPaddingTop();
                int iconPR = ivOwnerStatisticsIcon.getPaddingRight();
                int iconPB = ivOwnerStatisticsIcon.getPaddingBottom();
                ivOwnerStatisticsIcon.setBackgroundResource(R.drawable.action_dialog_owner_green);
                ivOwnerStatisticsIcon.setPadding(iconPL, iconPT, iconPR, iconPB);

                TableRow trOwnerStatisticEmployeeTitle = (TableRow) statisticsView.findViewById(R.id.trOwnerStatisticEmployeeTitle);
                trOwnerStatisticEmployeeTitle.setBackgroundColor(getResources().getColor(R.color.dialog_action_owner_green));
                TextView tvDialogStatisticsTitle = (TextView) statisticsView.findViewById(R.id.tvDialogStatisticsTitle);
                TextView tvStatisticsFirstTh = (TextView) statisticsView.findViewById(R.id.tvStatisticsFirstTh);
                tvStatisticsFirstTh.setText(getResources().getString(R.string.statistics_table_drink_name_title));
                tvStatisticsSize = (TextView) statisticsView.findViewById(R.id.tvStatisticsSize);
                tvDialogStatisticsTitle.setText(getResources().getString(R.string.statistics_title_drinks));

                Button btnDialogStatisticsNumbers = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsNumbers);
                Button btnDialogStatisticsPercentages = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsPercentages);
                Button btnDialogStatisticsTable = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsTable);
                Button btnDialogStatisticsSize = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsSize);

                EditText etStatisticQuerySize = (EditText) statisticsView.findViewById(R.id.etStatisticQuerySize);
                ImageView ivStatisticQuerySize = (ImageView) statisticsView.findViewById(R.id.ivStatisticQuerySize);

                tlStatisticEmployees = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployees);
                tlStatisticEmployeesTitle = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployeesTitle);

                pieChart = (PieChart) statisticsView.findViewById(R.id.pcDialogStatistics);
                pieChart.getDescription().setText(getResources().getString(R.string.statistics_drinks_chart_description));
                List<PieEntry> pieEntryList = new ArrayList<>();

                final AlertDialog statisticsDialog = new AlertDialog.Builder(getActivity())
                        .setView(statisticsView)
                        .create();
                statisticsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                statisticsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        databaseQuery(databaseQuerySize, 1);
                        btnDialogStatisticsNumbers.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = true;
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
                                makePieChart(false);
                            }
                        });

                        btnDialogStatisticsPercentages.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = true;
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));

                                tlStatisticEmployeesTitle.setVisibility(View.GONE);
                                pieChart.setVisibility(View.VISIBLE);

                                makePieChart(true);
                            }
                        });

                        btnDialogStatisticsTable.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = false;
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));

                                pieChart.setVisibility(View.GONE);
                                tlStatisticEmployeesTitle.setVisibility(View.VISIBLE);
                                populateEmployeesTable();
                            }
                        });

                        btnDialogStatisticsSize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(btnDialogStatisticsSize.getBackgroundTintList() == getResources().getColorStateList(R.color.light_tumbleweed)) {
                                    btnDialogStatisticsSize.setBackgroundTintList(getResources().getColorStateList(R.color.tumbleweed));
                                    etStatisticQuerySize.setVisibility(View.GONE);
                                    etStatisticQuerySize.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                                    ivStatisticQuerySize.setVisibility(View.GONE);
                                }
                                else {
                                    btnDialogStatisticsSize.setBackgroundTintList(getResources().getColorStateList(R.color.light_tumbleweed));
                                    etStatisticQuerySize.setVisibility(View.VISIBLE);
                                    ivStatisticQuerySize.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        ivStatisticQuerySize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(etStatisticQuerySize.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                    toastMessage.showToast(getResources().getString(R.string.statistics_query_size_failed), 0);
                                }
                                else if(Integer.parseInt(etStatisticQuerySize.getText().toString()) <= 0) {
                                    toastMessage.showToast(getResources().getString(R.string.statistics_query_size_failed), 0);
                                }
                                else {
                                    pieChartStatistic = new HashMap<>();
                                    databaseQuery(Integer.parseInt(etStatisticQuerySize.getText().toString()), 1);
                                }
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

    private void btnTablesClickEvent() {
        btnStatisticsTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChart = true;
                View statisticsView = getLayoutInflater().inflate(R.layout.dialog_statistics_chart, null);
                LinearLayoutCompat llStatisticsContentContainer = (LinearLayoutCompat) statisticsView.findViewById(R.id.llStatisticsContentContainer);
                int containerPL = llStatisticsContentContainer.getPaddingLeft();
                int containerPT = llStatisticsContentContainer.getPaddingTop();
                int containerPR = llStatisticsContentContainer.getPaddingRight();
                int containerPB = llStatisticsContentContainer.getPaddingBottom();
                llStatisticsContentContainer.setBackgroundResource(R.drawable.action_dialog_owner_purple);
                llStatisticsContentContainer.setPadding(containerPL, containerPT, containerPR, containerPB);

                ImageView ivOwnerStatisticsIcon = (ImageView) statisticsView.findViewById(R.id.ivOwnerStatisticsIcon);
                int iconPL = ivOwnerStatisticsIcon.getPaddingLeft();
                int iconPT = ivOwnerStatisticsIcon.getPaddingTop();
                int iconPR = ivOwnerStatisticsIcon.getPaddingRight();
                int iconPB = ivOwnerStatisticsIcon.getPaddingBottom();
                ivOwnerStatisticsIcon.setBackgroundResource(R.drawable.action_dialog_owner_purple);
                ivOwnerStatisticsIcon.setPadding(iconPL, iconPT, iconPR, iconPB);

                TableRow trOwnerStatisticEmployeeTitle = (TableRow) statisticsView.findViewById(R.id.trOwnerStatisticEmployeeTitle);
                trOwnerStatisticEmployeeTitle.setBackgroundColor(getResources().getColor(R.color.dialog_action_owner_purple));
                TextView tvDialogStatisticsTitle = (TextView) statisticsView.findViewById(R.id.tvDialogStatisticsTitle);
                TextView tvStatisticsFirstTh = (TextView) statisticsView.findViewById(R.id.tvStatisticsFirstTh);
                tvStatisticsFirstTh.setText(getResources().getString(R.string.statistics_table_table_number));
                tvStatisticsSize = (TextView) statisticsView.findViewById(R.id.tvStatisticsSize);
                tvDialogStatisticsTitle.setText(getResources().getString(R.string.statistics_title_tables));

                Button btnDialogStatisticsNumbers = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsNumbers);
                Button btnDialogStatisticsPercentages = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsPercentages);
                Button btnDialogStatisticsTable = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsTable);
                Button btnDialogStatisticsSize = (Button) statisticsView.findViewById(R.id.btnDialogStatisticsSize);

                EditText etStatisticQuerySize = (EditText) statisticsView.findViewById(R.id.etStatisticQuerySize);
                ImageView ivStatisticQuerySize = (ImageView) statisticsView.findViewById(R.id.ivStatisticQuerySize);

                tlStatisticEmployees = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployees);
                tlStatisticEmployeesTitle = (TableLayout) statisticsView.findViewById(R.id.tlOwnerStatisticEmployeesTitle);

                pieChart = (PieChart) statisticsView.findViewById(R.id.pcDialogStatistics);
                pieChart.getDescription().setText(getResources().getString(R.string.statistics_tables_chart_description));
                List<PieEntry> pieEntryList = new ArrayList<>();

                final AlertDialog statisticsDialog = new AlertDialog.Builder(getActivity())
                        .setView(statisticsView)
                        .create();
                statisticsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                statisticsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        databaseQuery(databaseQuerySize, 2);
                        btnDialogStatisticsNumbers.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = true;
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
                                makePieChart(false);
                            }
                        });

                        btnDialogStatisticsPercentages.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = true;
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));

                                tlStatisticEmployeesTitle.setVisibility(View.GONE);
                                pieChart.setVisibility(View.VISIBLE);

                                makePieChart(true);
                            }
                        });

                        btnDialogStatisticsTable.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showChart = false;
                                btnDialogStatisticsPercentages.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsNumbers.setBackgroundTintList(getResources().getColorStateList(R.color.pewter_blue));
                                btnDialogStatisticsTable.setBackgroundTintList(getResources().getColorStateList(R.color.light_pewter_blue));

                                pieChart.setVisibility(View.GONE);
                                tlStatisticEmployeesTitle.setVisibility(View.VISIBLE);
                                populateEmployeesTable();
                            }
                        });

                        btnDialogStatisticsSize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(btnDialogStatisticsSize.getBackgroundTintList() == getResources().getColorStateList(R.color.light_tumbleweed)) {
                                    btnDialogStatisticsSize.setBackgroundTintList(getResources().getColorStateList(R.color.tumbleweed));
                                    etStatisticQuerySize.setVisibility(View.GONE);
                                    etStatisticQuerySize.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                                    ivStatisticQuerySize.setVisibility(View.GONE);
                                }
                                else {
                                    btnDialogStatisticsSize.setBackgroundTintList(getResources().getColorStateList(R.color.light_tumbleweed));
                                    etStatisticQuerySize.setVisibility(View.VISIBLE);
                                    ivStatisticQuerySize.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        ivStatisticQuerySize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(etStatisticQuerySize.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                    toastMessage.showToast(getResources().getString(R.string.statistics_query_size_failed), 0);
                                }
                                else if(Integer.parseInt(etStatisticQuerySize.getText().toString()) <= 0) {
                                    toastMessage.showToast(getResources().getString(R.string.statistics_query_size_failed), 0);
                                }
                                else {
                                    pieChartStatistic = new HashMap<>();
                                    databaseQuery(Integer.parseInt(etStatisticQuerySize.getText().toString()), 2);
                                }
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

    private void databaseQuery(int querySize, int queryType) {
        final int QUERY_TYPE_EMPLOYEES = AppConstValue.statisticsConstValue.QUERY_TYPE_EMPLOYEES;
        final int QUERY_TYPE_DRINKS = AppConstValue.statisticsConstValue.QUERY_TYPE_DRINKS;
        final int QUERY_TYPE_TABLES = AppConstValue.statisticsConstValue.QUERY_TYPE_TABLES;
        DatabaseReference cafeBillsRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeBillsOwner(mainViewModel.getOwnerCafeId().getValue()));
        Query queryCafeBills = cafeBillsRef.limitToLast(querySize);
        queryCafeBills.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeBillsSnapshot) {
                if(!cafeBillsSnapshot.exists()) {
                    return;
                }
                tvStatisticsSize.setText(
                        String.valueOf(cafeBillsSnapshot.getChildrenCount()) );
                for(DataSnapshot cafeBillSnapshot : cafeBillsSnapshot.getChildren()) {
                    if(!cafeBillSnapshot.exists()) {
                        return;
                    }
                    CafeBill cafeBill = cafeBillSnapshot.getValue(CafeBill.class);
                    switch (queryType) {
                        case QUERY_TYPE_EMPLOYEES: {
                            if(pieChartStatistic == null || pieChartStatistic.isEmpty()) {
                                pieChartStatistic.put(cafeBill.getCafeBillDelivererEmployee(), 1);
                            }
                            else {
                                if(pieChartStatistic.containsKey(cafeBill.getCafeBillDelivererEmployee())) {
                                    pieChartStatistic.put(cafeBill.getCafeBillDelivererEmployee(), pieChartStatistic.get(
                                            cafeBill.getCafeBillDelivererEmployee()) + 1);
                                }
                                else {
                                    pieChartStatistic.put(cafeBill.getCafeBillDelivererEmployee(), 1);
                                }
                            }
                            break;
                        }
                        case QUERY_TYPE_DRINKS: {
                            for(String cafeBillDrinkKey : cafeBill.getCafeBillDrinks().keySet()) {
                                CafeBillDrink cafeBillDrink = cafeBill.getCafeBillDrinks().get(cafeBillDrinkKey);
                                if(pieChartStatistic == null || pieChartStatistic.isEmpty()) {
                                    pieChartStatistic.put(cafeBillDrink.getDrinkName(), cafeBillDrink.getDrinkAmount());
                                }
                                else {
                                    if(pieChartStatistic.containsKey(cafeBillDrink.getDrinkName())) {
                                        pieChartStatistic.put(cafeBillDrink.getDrinkName(), pieChartStatistic.get(
                                                cafeBillDrink.getDrinkName()) + cafeBillDrink.getDrinkAmount());
                                    }
                                    else {
                                        pieChartStatistic.put(cafeBillDrink.getDrinkName(), cafeBillDrink.getDrinkAmount());
                                    }
                                }
                            }
                            break;
                        }
                        case QUERY_TYPE_TABLES: {
                            if(pieChartStatistic == null || pieChartStatistic.isEmpty()) {
                                pieChartStatistic.put(getResources().getString(R.string.statistics_tables_table_number) +
                                        AppConstValue.characterConstValue.CHARACTER_SPACING + String.valueOf(cafeBill.getCafeBillTableNumber()), 1);
                            }
                            else {
                                if(pieChartStatistic.containsKey(getResources().getString(R.string.statistics_tables_table_number) +
                                        AppConstValue.characterConstValue.CHARACTER_SPACING + String.valueOf(cafeBill.getCafeBillTableNumber()))) {
                                    pieChartStatistic.put(getResources().getString(R.string.statistics_tables_table_number) +
                                            AppConstValue.characterConstValue.CHARACTER_SPACING + String.valueOf(cafeBill.getCafeBillTableNumber()), pieChartStatistic.get(
                                            getResources().getString(R.string.statistics_tables_table_number) +
                                                    AppConstValue.characterConstValue.CHARACTER_SPACING + String.valueOf(cafeBill.getCafeBillTableNumber())) + 1);
                                }
                                else {
                                    pieChartStatistic.put(getResources().getString(R.string.statistics_tables_table_number) +
                                            AppConstValue.characterConstValue.CHARACTER_SPACING + String.valueOf(cafeBill.getCafeBillTableNumber()), 1);
                                }
                            }
                            break;
                        }
                    }
                }
                if(showChart) {
                    makePieChart(false);
                }
                else {
                    populateEmployeesTable();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }


    private void makePieChart(boolean showPercentage) {
        if(pieChartStatistic == null || pieChartStatistic.isEmpty()) {
            return;
        }
        List<PieEntry> pieEntryList  = new ArrayList<>();
        for(String key : sortStatisticsEntry(pieChartStatistic, true).keySet()) {
            pieEntryList.add(new PieEntry(pieChartStatistic.get(key), key));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, AppConstValue.statisticsConstValue.PIE_CHART_LABEL);
        pieData = new PieData(pieDataSet);

        if(showPercentage) {
            float statisticsValuesSum = 0;
            for(String key : sortStatisticsEntry(pieChartStatistic, true).keySet()) {
                statisticsValuesSum += pieChartStatistic.get(key);
            }
            final float statisticsSum = statisticsValuesSum;
            pieData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    DecimalFormat percentageDecimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.DECIMAL_PERCENTAGE, decimalFormatSymbols);
                    double percent = (value / statisticsSum);
                    return percentageDecimalFormat.format(percent);
                }
            });
        }
        else {
            pieData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) Math.floor(value));
                }
            });
        }

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieDataSet.setColors(statisticsPieChartColors.getPieChartColors());
        pieData.setValueTextSize(12f);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setTextSize(12f);
        pieChart.getDescription().setTextColor(getResources().getColor(R.color.black));
        pieChart.setNoDataTextColor(getResources().getColor(R.color.black));
        pieChart.setExtraBottomOffset(2f);
        pieChart.setHoleColor(getResources().getColor(R.color.cv_cafe_update_blue_overlay));
        pieChart.invalidate();
    }

    private void populateEmployeesTable() {
        while(tlStatisticEmployees.getChildCount() > 0) {
            tlStatisticEmployees.removeView(tlStatisticEmployees.getChildAt(tlStatisticEmployees.getChildCount() - 1));
        }

        float statisticsValuesSum = 0;
        for(String key : sortStatisticsEntry(pieChartStatistic, false).keySet()) {
            statisticsValuesSum += pieChartStatistic.get(key);
        }
        for(String key : sortStatisticsEntry(pieChartStatistic, false).keySet()) {
            View trEmployeeView = getLayoutInflater().inflate(R.layout.owner_statistics_row, tlStatisticEmployees, false);

            TextView tvStatisticEmployeePhoneNumber = (TextView) trEmployeeView.findViewById(R.id.tvStatisticEmployeePhoneNumber);
            TextView tvStatisticEmployeeValue = (TextView) trEmployeeView.findViewById(R.id.tvStatisticEmployeeValue);
            TextView tvStatisticEmployeePercent = (TextView) trEmployeeView.findViewById(R.id.tvStatisticEmployeePercent);

            float emplyoeePercent = (float) pieChartStatistic.get(key) / statisticsValuesSum;
            tvStatisticEmployeePhoneNumber.setText(key);
            tvStatisticEmployeeValue.setText(String.valueOf(pieChartStatistic.get(key)));
            tvStatisticEmployeePercent.setText(cafeDecimalFormat.format(emplyoeePercent * 100) + AppConstValue.characterConstValue.PERCENTAGE);
            tlStatisticEmployees.addView(trEmployeeView);
        }
    }

    private LinkedHashMap<String, Integer> sortStatisticsEntry(HashMap<String, Integer> statEntries, boolean counterNeeded) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(statEntries.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> firstValue, Map.Entry<String, Integer> secondValue) {
                return secondValue.getValue().compareTo(firstValue.getValue());
            }
        });

        LinkedHashMap<String, Integer> sortedHashMap = new LinkedHashMap<>();
        if(counterNeeded) {
            int count = 0;
            for (Map.Entry<String, Integer> entry : list) {
                if (count >= 10) {
                    break;
                }
                sortedHashMap.put(entry.getKey(), entry.getValue());
                count++;
            }
        }
        else {
            for (Map.Entry<String, Integer> entry : list) {
                sortedHashMap.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedHashMap;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}