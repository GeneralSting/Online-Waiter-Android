package com.example.onlinewaiter.employeeUI.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.Adapter.OrderDrinksAdapter;
import com.example.onlinewaiter.EmployeeActivity;
import com.example.onlinewaiter.Interfaces.CallBackOrder;
import com.example.onlinewaiter.Models.CafeBill;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentOrderBinding;
import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
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
    OrderDrinksAdapter orderDrinksAdapter;

    //firebase
    DatabaseReference cafeDrinksCategoriesRef, categoryDrinksRef, newCafeBillRef;
    FirebaseDatabase firebaseDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        toastMessage = new ToastMessage(getActivity());
        firebaseRefPaths = new FirebaseRefPaths();

        tvOrderBillAmount = (TextView)binding.tvOrderAmount;
        tvOrderBillTotalPrice = (TextView)binding.tvOrderTotalPrice;
        tvOrderBillTotalPrice.setText(getResources().getString(R.string.order_summary_total_price_zero)
                + getResources().getString(R.string.country_currency));
        btnOrderAccept = (Button) binding.btnOrderAccept;
        ibtnOrderCancel = (ImageButton) binding.ibtnOrderCancel;
        disableOrderConfirm();

        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        orderDrinks = orderViewModel.getDrinksInOrder().getValue();
        if(orderDrinks != null && !orderDrinks.isEmpty()) {
            modifiedOrderDrinks = new HashMap<String, CafeBillDrink>();
            insertOrderDrinks();
        }

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
        cafeDrinksCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getRefPathCafes() +
                menuViewModel.getCafeId().getValue() + firebaseRefPaths.getRefPathCafeDrinksCategories());
        cafeDrinksCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categoriesSnapshot) {
                for(DataSnapshot drinksCategory: categoriesSnapshot.getChildren()) {
                    checkDrinksByCategory(drinksCategory.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
            }
        });
    }

    private void checkDrinksByCategory(String drinksCategoryId) {
        categoryDrinksRef = firebaseDatabase.getReference(firebaseRefPaths.getRefPathCafes() +
                menuViewModel.getCafeId().getValue() + firebaseRefPaths.getRefPathCafeDrinksCategories() +
                drinksCategoryId + firebaseRefPaths.getRefPathCategoryDrinks());
        categoryDrinksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categoryDrinksSnapshot) {
                for(DataSnapshot snapshotCategoryDrink : categoryDrinksSnapshot.getChildren()) {
                    boolean addNewCategoryDrink = false;
                    CafeBillDrink newCafeBillDrink = new CafeBillDrink();
                    for(String key : orderDrinks.keySet()) {
                        CafeBillDrink cafeBillDrink = orderDrinks.get(key);
                        if(Objects.equals(cafeBillDrink.getDrinkId(), snapshotCategoryDrink.getKey())) {
                            CategoryDrink categoryDrink = snapshotCategoryDrink.getValue(CategoryDrink.class);
                            CafeBillDrink cafeBillDrinkCopy = new CafeBillDrink(
                                    cafeBillDrink.getDrinkId(),
                                    categoryDrink.getCategoryDrinkName(),
                                    categoryDrink.getCategoryDrinkImage(),
                                    categoryDrink.getCategoryDrinkPrice(),
                                    ((Float) categoryDrink.getCategoryDrinkPrice() * cafeBillDrink.getDrinkAmount()),
                                    cafeBillDrink.getDrinkAmount()
                            );
                            addNewCategoryDrink = true;
                            newCafeBillDrink = cafeBillDrinkCopy;
                        }
                    }
                    if(addNewCategoryDrink) {
                        modifiedOrderDrinks.put(newCafeBillDrink.getDrinkId(), newCafeBillDrink);
                        if(modifiedOrderDrinks.size() == orderDrinks.size()) {
                            displayOrderDrinks();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
            }
        });
    }

    private void displayOrderDrinks() {
        updateOrderSummary(modifiedOrderDrinks);
        rvOrderDrinks = (RecyclerView)binding.rvOrderDrinks;
        rvOrderDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        orderDrinksAdapter = new OrderDrinksAdapter(getContext(), modifiedOrderDrinks, this);
        rvOrderDrinks.setAdapter(orderDrinksAdapter);
        orderDrinksAdapter.notifyDataSetChanged();
        btnOrderAccept.setEnabled(true);
        btnOrderAccept.setBackgroundColor(getResources().getColor(R.color.green_positive));
        btnOrderAccept.setTextColor(getResources().getColor(R.color.white));
        ibtnOrderCancel.setEnabled(true);
    }

    private void updateOrderSummary(HashMap<String, CafeBillDrink> currentOrderDrinks) {
        if(currentOrderDrinks == null || currentOrderDrinks.isEmpty()) {
            tvOrderBillAmount.setText(getResources().getString(R.string.order_summary_amount_empty));
            tvOrderBillTotalPrice.setText(getResources().getString(R.string.order_summary_total_price_zero)
                    + getResources().getString(R.string.country_currency));
            disableOrderConfirm();
        }
        else {
            Float orderTotalPrice = 0f;
            int orderProductsAmount = 0;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            for(String key : currentOrderDrinks.keySet()) {
                CafeBillDrink cafeBillDrink = currentOrderDrinks.get(key);
                orderTotalPrice += (Float) cafeBillDrink.getDrinkTotalPrice();
                orderProductsAmount += (int) cafeBillDrink.getDrinkAmount();
            }
            cafeBillDrinkTotalPrice = String.format(Locale.US, "%.2f", orderTotalPrice);
            cafeBillDrinkAmount = (int) orderProductsAmount;
            cafeBillDrinks = (HashMap<String, CafeBillDrink>) currentOrderDrinks;
            tvOrderBillAmount.setText(orderProductsAmount + " " + getResources().getString(R.string.order_summary_amount));
            tvOrderBillTotalPrice.setText(decimalFormat.format(orderTotalPrice) + getResources().getString(R.string.country_currency));
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
        rvOrderDrinks.setAdapter(null);
        updateOrderDrinks(new HashMap<>());
    }

    private void finishOrder() {
        View completeOrderView = getLayoutInflater().inflate(R.layout.order_completion_dialog, null);
        NumberPicker npTableNumber = completeOrderView.findViewById(R.id.npOrderDialogTableNumber);
        npTableNumber.setMinValue(1);
        npTableNumber.setMaxValue(orderViewModel.getCafeTablesNumber().getValue());
        Button btnOrderAccept = completeOrderView.findViewById(R.id.btnOrderDialogAccept);
        Button btnOrderCancel = completeOrderView.findViewById(R.id.btnOrderDialogCancel);
        CheckBox cbMyOrder = completeOrderView.findViewById(R.id.cbMyOrder);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(completeOrderView)
                .setTitle(getResources().getString(R.string.order_dialog_title))
                .create();

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
                btnOrderCancel.setOnClickListener(new View.OnClickListener() {
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
        newCafeBillRef = firebaseDatabase.getReference(firebaseRefPaths.getRefPathCafes() + menuViewModel.getCafeId().getValue() +
                firebaseRefPaths.getRefPathCafesBills());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = simpleDateFormat.format(new Date());
        HashMap<String, CafeBillDrink> orderDrinks = new HashMap<>();

        CafeBill cafeBill = new CafeBill(
                currentDateTime,
                cafeBillDrinkTotalPrice,
                menuViewModel.getPhoneNumber().getValue(),
                cafeBillDrinkAmount,
                tableNumber,
                cafeBillDrinks
        );
        String dbKey = newCafeBillRef.push().getKey();
        toastMessage.showToast(String.valueOf(myOrder), 0);
        removeOrderDrinks();
        newCafeBillRef.child(dbKey).setValue(cafeBill);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}