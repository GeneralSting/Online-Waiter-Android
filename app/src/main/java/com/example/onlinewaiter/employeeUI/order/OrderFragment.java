package com.example.onlinewaiter.employeeUI.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.Adapter.OrderDrinksAdapter;
import com.example.onlinewaiter.EmployeeActivity;
import com.example.onlinewaiter.Filters.InputMinMaxFilter;
import com.example.onlinewaiter.Interfaces.CallBackOrder;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Models.CafeCurrentOrder;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.CustomAlertDialog;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentOrderBinding;
import com.example.onlinewaiter.employeeUI.GlobalViewModel.EmployeeViewModel;
import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.SortedSet;
import java.util.TreeSet;

public class OrderFragment extends Fragment implements CallBackOrder {

    //fragment views
    private RecyclerView rvOrderDrinks;
    private TextView tvOrderBillAmount, tvOrderBillTotalPrice;
    private Button btnOrderAccept;
    private ImageButton btnOrderCancel;

    //global variables/objects
    private FragmentOrderBinding binding;
    private int cafeBillDrinkAmount;
    private String cafeBillDrinkTotalPrice;
    private HashMap<String, CafeBillDrink> orderDrinks, modifiedOrderDrinks, cafeBillDrinks;
    private MenuViewModel menuViewModel;
    private OrderViewModel orderViewModel;
    private EmployeeViewModel employeeViewModel;
    private AlertDialog dialog;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private AppError appError;
    private DecimalFormat cafeDecimalFormat;

    //firebase
    private FirebaseDatabase firebaseDatabase;
    private FirebaseRefPaths firebaseRefPaths;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseRefPaths = new FirebaseRefPaths(getActivity());
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(Objects.requireNonNull(employeeViewModel.getCafeDecimalSeperator().getValue()).charAt(0));
        cafeDecimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO, decimalFormatSymbols);

        tvOrderBillAmount = (TextView)binding.tvOrderAmount;
        tvOrderBillTotalPrice = (TextView)binding.tvOrderTotalPrice;
        tvOrderBillTotalPrice.setText(cafeDecimalFormat.format(AppConstValue.variableConstValue.FLOAT_PRICE_ZERO));
        btnOrderAccept = (Button) binding.btnOrderAccept;
        btnOrderCancel = (ImageButton) binding.btnOrderCancel;

        rvOrderDrinks = (RecyclerView)binding.rvOrderDrinks;
        rvOrderDrinks.setLayoutManager(new LinearLayoutManager(getContext()));

        disableOrderConfirm();
        orderDrinks = orderViewModel.getDrinksInOrder().getValue();
        if(orderDrinks != null && !orderDrinks.isEmpty()) {
            modifiedOrderDrinks = new HashMap<String, CafeBillDrink>();
            insertOrderDrinks();
        }
        drinksOrderObserver();
        orderBtnsActions();

        return root;
    }

    private void disableOrderConfirm() {
        btnOrderAccept.setEnabled(false);
        btnOrderAccept.setBackgroundColor(getResources().getColor(R.color.order_btn_disabled));
        btnOrderAccept.setTextColor(getResources().getColor(R.color.order_txt_disabled));
        btnOrderCancel.setEnabled(false);
    }

    private void insertOrderDrinks() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        for(String key : orderDrinks.keySet()) {
            CafeBillDrink cafeBillDrink = orderDrinks.get(key);
            DatabaseReference cafeDrinksCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getCategoryDrink(
                    cafeBillDrink.getCategoryId(), cafeBillDrink.getDrinkId()));
            cafeDrinksCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot drinkSnapshot) {
                    if(!drinkSnapshot.exists()) {
                        orderDrinks.remove(key);
                    }
                    else {
                        CategoryDrink categoryDrink = drinkSnapshot.getValue(CategoryDrink.class);
                        CafeBillDrink newCafeBillDrink = new CafeBillDrink(
                                key,
                                cafeBillDrink.getCategoryId(),
                                categoryDrink.getCategoryDrinkName(),
                                categoryDrink.getCategoryDrinkImage(),
                                categoryDrink.getCategoryDrinkPrice(),
                                ((Float) categoryDrink.getCategoryDrinkPrice() * cafeBillDrink.getDrinkAmount()),
                                cafeBillDrink.getDrinkAmount(),
                                cafeBillDrink.getDrinkQuantity()
                        );
                        modifiedOrderDrinks.put(newCafeBillDrink.getDrinkId(), newCafeBillDrink);
                    }
                    if(modifiedOrderDrinks.size() == orderDrinks.size()) {
                        displayOrderDrinks();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                    serverAlertDialog.makeAlertDialog();
                    String currentDateTime = simpleDateFormat.format(new Date());
                    appError = new AppError(
                            menuViewModel.getCafeId().getValue(),
                            menuViewModel.getPhoneNumber().getValue(),
                            AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                            error.getMessage().toString(),
                            currentDateTime
                    );
                    appError.sendError(appError);
                }
            });
        }
    }

    private void displayOrderDrinks() {
        if(isAdded()) {
            List<Map.Entry<String, CafeBillDrink>> entryList = new ArrayList<>(modifiedOrderDrinks.entrySet());
            entryList.sort(new Comparator<Map.Entry<String, CafeBillDrink>>() {
                @Override
                public int compare(Map.Entry<String, CafeBillDrink> firstEntry, Map.Entry<String, CafeBillDrink> secondEntry) {
                    String firstDrinkName = firstEntry.getValue().getDrinkName();
                    String secondDrinkName = secondEntry.getValue().getDrinkName();
                    return firstDrinkName.compareTo(secondDrinkName);
                }
            });
            LinkedHashMap<String, CafeBillDrink> sortedDrinks = new LinkedHashMap<>();
            for (Map.Entry<String, CafeBillDrink> entry : entryList) {
                sortedDrinks.put(entry.getKey(), entry.getValue());
            }

            updateOrderSummary(modifiedOrderDrinks);
            OrderDrinksAdapter orderDrinksAdapter = new OrderDrinksAdapter(getContext(), sortedDrinks, employeeViewModel.getCafeCurrency().getValue(), this);
            rvOrderDrinks.setAdapter(orderDrinksAdapter);
            orderDrinksAdapter.notifyDataSetChanged();
            btnOrderAccept.setEnabled(true);
            btnOrderAccept.setBackgroundColor(getResources().getColor(R.color.green_positive));
            btnOrderAccept.setTextColor(getResources().getColor(R.color.white));
            btnOrderCancel.setEnabled(true);
        }
    }

    private void updateOrderSummary(HashMap<String, CafeBillDrink> currentOrderDrinks) {
        if(isAdded()) {
            if(currentOrderDrinks == null || currentOrderDrinks.isEmpty()) {
                tvOrderBillAmount.setText(getResources().getString(R.string.order_summary_amount_empty));
                String zeroPrice = cafeDecimalFormat.format(AppConstValue.variableConstValue.FLOAT_PRICE_ZERO);
                tvOrderBillTotalPrice.setText(zeroPrice + employeeViewModel.getCafeCurrency().getValue());
                disableOrderConfirm();
            }
            else {
                Float orderTotalPrice = 0f;
                int orderProductsAmount = 0;
                for(String key : currentOrderDrinks.keySet()) {
                    CafeBillDrink cafeBillDrink = currentOrderDrinks.get(key);
                    orderTotalPrice += (Float) cafeBillDrink.getDrinkTotalPrice();
                    orderProductsAmount += (int) cafeBillDrink.getDrinkAmount();
                }
                cafeBillDrinkTotalPrice = cafeDecimalFormat.format(orderTotalPrice);
                cafeBillDrinkAmount = (int) orderProductsAmount;
                cafeBillDrinks = (HashMap<String, CafeBillDrink>) currentOrderDrinks;
                tvOrderBillAmount.setText(orderProductsAmount + AppConstValue.characterConstValue.CHARACTER_SPACING + getResources().getString(R.string.order_summary_amount));
                tvOrderBillTotalPrice.setText(cafeDecimalFormat.format(orderTotalPrice) + employeeViewModel.getCafeCurrency().getValue());
            }
        }
    }

    private void drinksOrderObserver() {
        final Observer<Integer> observeOrderDrinksChange = new Observer<Integer>() {
            @Override
            public void onChanged(Integer viewDisplayedDepth) {
                orderDrinks = orderViewModel.getDrinksInOrder().getValue();
                if(dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                if(orderDrinks != null && !orderDrinks.isEmpty()) {
                    modifiedOrderDrinks = new HashMap<String, CafeBillDrink>();
                    insertOrderDrinks();
                }
                else {
                    removeOrderDrinks();
                }
            }
        };
        orderViewModel.getCheckDrinksOrder().observe(requireActivity(), observeOrderDrinksChange);
    }

    private void orderBtnsActions() {
        btnOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOrderDrinks();
            }
        });
        btnOrderAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishOrder();
            }
        });
    }

    private void removeOrderDrinks() {
        if(isAdded()) {
            rvOrderDrinks.setAdapter(null);
            updateOrderDrinks(new HashMap<>());
        }
    }

    @Override
    public void updateOrderDrinks(HashMap<String, CafeBillDrink> currentOrderDrinks) {
        orderViewModel.setDrinksInOrder(currentOrderDrinks);
        updateOrderSummary(currentOrderDrinks);
    }

    private void finishOrder() {
        View completeOrderView = getLayoutInflater().inflate(R.layout.order_completion_dialog, null);
        ImageButton ibCloseDialog = completeOrderView.findViewById(R.id.ibCloseOrderCompletion);
        NumberPicker npTableNumber = completeOrderView.findViewById(R.id.npOrderDialogTableNumber);
        EditText etTableNumber = completeOrderView.findViewById(R.id.etOrderDialogTableNumber);
        if(orderViewModel.getCafeTablesNumber().getValue() > 20) {
            npTableNumber.setVisibility(View.GONE);
            etTableNumber.setHint(getResources().getString(R.string.order_table_number_hint) + String.valueOf(orderViewModel.getCafeTablesNumber().getValue()));
            etTableNumber.setFilters(new InputFilter[]{new InputMinMaxFilter(
                    String.valueOf(AppConstValue.variableConstValue.MIN_TABLE_NUMBER),
                    String.valueOf(orderViewModel.getCafeTablesNumber().getValue()
            ))});
        }
        else {
            etTableNumber.setVisibility(View.GONE);
            npTableNumber.setMinValue(1);
            npTableNumber.setMaxValue(orderViewModel.getCafeTablesNumber().getValue());
        }
        Button btnOrderAccept = completeOrderView.findViewById(R.id.btnOrderDialogAccept);
        CheckBox cbMyOrder = completeOrderView.findViewById(R.id.cbMyOrder);
        dialog = new AlertDialog.Builder(getActivity())
                .setView(completeOrderView)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                btnOrderAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(orderViewModel.getCafeTablesNumber().getValue() > 20) {
                            if(etTableNumber.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                ToastMessage toastMessage = new ToastMessage(getActivity());
                                toastMessage.showToast(getResources().getString(R.string.order_table_number_empty), 0);
                            }
                            else {
                                saveOrder(Integer.parseInt(etTableNumber.getText().toString()), cbMyOrder.isChecked());
                                dialog.dismiss();
                            }
                        }
                        else {
                            saveOrder(npTableNumber.getValue(), cbMyOrder.isChecked());
                            dialog.dismiss();
                        }
                    }
                });

                ibCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void saveOrder(int tableNumber, boolean myOrder) {
        updateDrinksQuantity();
        DatabaseReference newCafeBillRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCurrentOrders());
        SimpleDateFormat simpleDateFormatLocale = new SimpleDateFormat(employeeViewModel.getCafeDateTimeFormat().getValue(), Locale.getDefault());
        String currentDateTime = simpleDateFormatLocale.format(new Date());

        CafeCurrentOrder cafeCurrentOrder;
        if(myOrder) {
            cafeCurrentOrder = new CafeCurrentOrder(
                    currentDateTime,
                    cafeBillDrinkTotalPrice,
                    menuViewModel.getPhoneNumber().getValue(),
                    menuViewModel.getPhoneNumber().getValue(),
                    cafeBillDrinkAmount,
                    tableNumber,
                    cafeBillDrinks,
                    AppConstValue.orderStatusConstValue.ORDER_STATUS_PENDING
            );
        }
        else {
            cafeCurrentOrder = new CafeCurrentOrder(
                    currentDateTime,
                    cafeBillDrinkTotalPrice,
                    menuViewModel.getPhoneNumber().getValue(),
                    cafeBillDrinkAmount,
                    tableNumber,
                    cafeBillDrinks,
                    AppConstValue.orderStatusConstValue.ORDER_STATUS_PENDING
            );
        }
        String dbKey = newCafeBillRef.push().getKey();
        removeOrderDrinks();
        newCafeBillRef.child(dbKey).setValue(cafeCurrentOrder);
    }

    private void updateDrinksQuantity() {
        for(String key : orderDrinks.keySet()) {
            CafeBillDrink cafeBillDrink = orderDrinks.get(key);
            DatabaseReference categroyDrinkQuantity = firebaseDatabase.getReference(firebaseRefPaths.getCategoryDrinkQuantity(
                    cafeBillDrink.getCategoryId(), cafeBillDrink.getDrinkId()));
            categroyDrinkQuantity.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot drinkQuantitySnapshot) {
                    int newQuantity = drinkQuantitySnapshot.getValue(Integer.class) - cafeBillDrink.getDrinkAmount();
                    if(newQuantity < 0) {
                        newQuantity = 0;
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(requireActivity(),
                                getResources().getString(R.string.act_employee_drink_negative_quantity_title),
                                getResources().getString(R.string.act_employee_drink_negative_quantity_body),
                                getResources().getDrawable(R.drawable.modal_no_quantity));
                        customAlertDialog.makeAlertDialog();

                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                menuViewModel.getCafeId().getValue(),
                                menuViewModel.getPhoneNumber().getValue(),
                                AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                                AppErrorMessages.ErrorsMessage.DRINK_QUANTITY_UNDER_ZERO,
                                currentDateTime
                        );
                        appError.sendError(appError);
                    }
                    categroyDrinkQuantity.setValue(newQuantity);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                    serverAlertDialog.makeAlertDialog();
                    String currentDateTime = simpleDateFormat.format(new Date());
                    appError = new AppError(
                            menuViewModel.getCafeId().getValue(),
                            menuViewModel.getPhoneNumber().getValue(),
                            AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                            error.getMessage().toString(),
                            currentDateTime
                    );
                    appError.sendError(appError);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}