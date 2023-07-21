package com.example.onlinewaiter.employeeUI.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.onlinewaiter.Interfaces.CallBackOrder;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Models.CafeCurrentOrder;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class OrderFragment extends Fragment implements CallBackOrder {

    //fragment views
    RecyclerView rvOrderDrinks;
    TextView tvOrderBillAmount, tvOrderBillTotalPrice;
    Button btnOrderAccept;
    ImageButton ibtnOrderCancel;

    //global variables/objects
    private FragmentOrderBinding binding;
    HashMap<String, CafeBillDrink> orderDrinks, modifiedOrderDrinks, cafeBillDrinks;
    int cafeBillDrinkAmount;
    String cafeBillDrinkTotalPrice;
    ToastMessage toastMessage;
    FirebaseRefPaths firebaseRefPaths;
    MenuViewModel menuViewModel;
    OrderViewModel orderViewModel;
    private EmployeeViewModel employeeViewModel;
    OrderDrinksAdapter orderDrinksAdapter;
    AlertDialog dialog;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
    SimpleDateFormat simpleDateLocaleFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_CRO, Locale.getDefault());
    private AppError appError;


    //firebase
    DatabaseReference cafeDrinksCategoriesRef, categoryDrinksRef, newCafeBillRef;
    FirebaseDatabase firebaseDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        toastMessage = new ToastMessage(getActivity());
        firebaseRefPaths = new FirebaseRefPaths(getActivity());

        tvOrderBillAmount = (TextView)binding.tvOrderAmount;
        tvOrderBillTotalPrice = (TextView)binding.tvOrderTotalPrice;
        tvOrderBillTotalPrice.setText(getResources().getString(R.string.order_summary_total_price_zero)
                + getResources().getString(R.string.country_currency));
        btnOrderAccept = (Button) binding.btnOrderAccept;
        ibtnOrderCancel = (ImageButton) binding.ibtnOrderCancel;

        rvOrderDrinks = (RecyclerView)binding.rvOrderDrinks;
        rvOrderDrinks.setLayoutManager(new LinearLayoutManager(getContext()));

        disableOrderConfirm();

        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);
        orderDrinks = orderViewModel.getDrinksInOrder().getValue();
        if(orderDrinks != null && !orderDrinks.isEmpty()) {
            modifiedOrderDrinks = new HashMap<String, CafeBillDrink>();
            insertOrderDrinks();
        }

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

        ibtnOrderCancel.setOnClickListener(new View.OnClickListener() {
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
        return root;
    }

    private void insertOrderDrinks() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        for(String key : orderDrinks.keySet()) {
            CafeBillDrink cafeBillDrink = orderDrinks.get(key);
            cafeDrinksCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrink(
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
                                cafeBillDrink.getDrinkId(),
                                categoryDrink.getCategoryDrinkName(),
                                categoryDrink.getCategoryDrinkImage(),
                                categoryDrink.getCategoryDrinkPrice(),
                                ((Float) categoryDrink.getCategoryDrinkPrice() * cafeBillDrink.getDrinkAmount()),
                                cafeBillDrink.getDrinkAmount()
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
            updateOrderSummary(modifiedOrderDrinks);
            orderDrinksAdapter = new OrderDrinksAdapter(getContext(), modifiedOrderDrinks, this);
            rvOrderDrinks.setAdapter(orderDrinksAdapter);
            orderDrinksAdapter.notifyDataSetChanged();
            btnOrderAccept.setEnabled(true);
            btnOrderAccept.setBackgroundColor(getResources().getColor(R.color.green_positive));
            btnOrderAccept.setTextColor(getResources().getColor(R.color.white));
            ibtnOrderCancel.setEnabled(true);
        }
    }

    private void updateOrderSummary(HashMap<String, CafeBillDrink> currentOrderDrinks) {
        if(isAdded()) {
            if(currentOrderDrinks == null || currentOrderDrinks.isEmpty()) {
                tvOrderBillAmount.setText(getResources().getString(R.string.order_summary_amount_empty));
                tvOrderBillTotalPrice.setText(getResources().getString(R.string.order_summary_total_price_zero)
                        + getResources().getString(R.string.country_currency));
                disableOrderConfirm();
            }
            else {
                Float orderTotalPrice = 0f;
                int orderProductsAmount = 0;
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator(Objects.requireNonNull(employeeViewModel.getCafeDecimalSeperator().getValue()).charAt(0));
                DecimalFormat cafeDecimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO, decimalFormatSymbols);
                for(String key : currentOrderDrinks.keySet()) {
                    CafeBillDrink cafeBillDrink = currentOrderDrinks.get(key);
                    orderTotalPrice += (Float) cafeBillDrink.getDrinkTotalPrice();
                    orderProductsAmount += (int) cafeBillDrink.getDrinkAmount();
                }
                cafeBillDrinkTotalPrice = String.format(Locale.US, "%.2f", orderTotalPrice);
                cafeBillDrinkAmount = (int) orderProductsAmount;
                cafeBillDrinks = (HashMap<String, CafeBillDrink>) currentOrderDrinks;
                tvOrderBillAmount.setText(orderProductsAmount + " " + getResources().getString(R.string.order_summary_amount));
                tvOrderBillTotalPrice.setText(cafeDecimalFormat.format(orderTotalPrice) + employeeViewModel.getCafeCurrency().getValue());
            }
        }
    }

    private void disableOrderConfirm() {
        btnOrderAccept.setEnabled(false);
        btnOrderAccept.setBackgroundColor(getResources().getColor(R.color.order_btn_disabled));
        btnOrderAccept.setTextColor(getResources().getColor(R.color.order_txt_disabled));
        ibtnOrderCancel.setEnabled(false);
    }

    @Override
    public void updateOrderDrinks(HashMap<String, CafeBillDrink> currentOrderDrinks) {
        orderViewModel.setDrinksInOrder(currentOrderDrinks);
        updateOrderSummary(currentOrderDrinks);
    }

    private void removeOrderDrinks() {
        if(isAdded()) {
            rvOrderDrinks.setAdapter(null);
            updateOrderDrinks(new HashMap<>());
        }
    }

    private void finishOrder() {
        View completeOrderView = getLayoutInflater().inflate(R.layout.order_completion_dialog, null);
        ImageButton ibCloseDialog = completeOrderView.findViewById(R.id.ibCloseOrderCompletion);
        NumberPicker npTableNumber = completeOrderView.findViewById(R.id.npOrderDialogTableNumber);
        npTableNumber.setMinValue(1);
        npTableNumber.setMaxValue(orderViewModel.getCafeTablesNumber().getValue());
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
                        saveOrder(npTableNumber.getValue(), cbMyOrder.isChecked());
                        dialog.dismiss();
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
        newCafeBillRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCurrentOrders());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_CRO, Locale.getDefault());
        String currentDateTime = simpleDateFormat.format(new Date());

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}