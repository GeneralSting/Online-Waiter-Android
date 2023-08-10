package com.example.onlinewaiter.employeeUI.pendingOrders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.Filters.DecimalPriceInputFilter;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.CafeBill;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CafeCurrentOrder;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessage;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.CurrentOrderViewHolder;
import com.example.onlinewaiter.databinding.FragmentPendingOrdersBinding;
import com.example.onlinewaiter.employeeUI.GlobalViewModel.EmployeeViewModel;
import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PendingOrdersFragment extends Fragment {
    //fragments views
    private TextView tvSwitchDescription;
    private RecyclerView rvCafeCurrentOrders;
    private SwitchCompat scCafeCurrentOrders;

    //global objects/variables
    private FragmentPendingOrdersBinding binding;
    private final int order_pending = AppConstValue.orderStatusConstValue.ORDER_STATUS_PENDING;
    private final int order_ready = AppConstValue.orderStatusConstValue.ORDER_STATUS_READY;
    private final int order_declined = AppConstValue.orderStatusConstValue.ORDER_STATUS_DECLINED;
    private final int order_removal_request = AppConstValue.orderStatusConstValue.ORDER_STATUS_REMOVAL_REQUEST;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private SimpleDateFormat simpleDateFormatLocale;
    private AppError appError;
    private MenuViewModel menuViewModel;
    private EmployeeViewModel employeeViewModel;
    private PendingOrdersViewModel pendingOrdersViewModel;
    private ToastMessage toastMessage;
    private DecimalFormat cafeDecimalFormat;


    //firebase
    private FirebaseRefPaths firebaseRefPaths;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference cafeBillsRef;
    private FirebaseRecyclerAdapter<CafeCurrentOrder, CurrentOrderViewHolder> adapterCurrentOrders;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPendingOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRefPaths = new FirebaseRefPaths(getActivity());
        pendingOrdersViewModel = new ViewModelProvider(requireActivity()).get(PendingOrdersViewModel.class);
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(Objects.requireNonNull(employeeViewModel.getCafeDecimalSeperator().getValue()).charAt(0));
        cafeDecimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO, decimalFormatSymbols);
        simpleDateFormatLocale = new SimpleDateFormat(employeeViewModel.getCafeDateTimeFormat().getValue(), Locale.getDefault());
        toastMessage = new ToastMessage(requireActivity());

        tvSwitchDescription = (TextView) binding.tvCCOSwitch;
        scCafeCurrentOrders = (SwitchCompat) binding.scCafeCurrentOrders;
        rvCafeCurrentOrders = (RecyclerView) binding.rvCafeCurrentOrders;
        RecyclerView.LayoutManager rvCurrentOrdersLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvCafeCurrentOrders.setLayoutManager(rvCurrentOrdersLayoutManager);

        ordersSwitch();
        searchedOrdersObserver();
        populateOrdersRv(Integer.parseInt(AppConstValue.variableConstValue.ZERO_VALUE));

        return root;
    }

    private void ordersSwitch() {
        scCafeCurrentOrders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(pendingOrdersViewModel.getSearchedOrder().getValue() != null) {
                    populateOrdersRv(pendingOrdersViewModel.getSearchedOrder().getValue());
                }
                if(b) {
                    tvSwitchDescription.setText(getResources().getString(R.string.pending_orders_switch_all));
                }
                else {
                    tvSwitchDescription.setText(getResources().getString(R.string.pending_orders_switch_mine));
                }
            }
        });
    }

    private void searchedOrdersObserver() {
        final Observer<Integer> observingSearchedDrinks = new Observer<Integer>() {
            @Override
            public void onChanged(Integer searchedOrder) {
                //not necessary
                //stopAdapterListening();
                populateOrdersRv(searchedOrder);
            }
        };
        pendingOrdersViewModel.getSearchedOrder().observe(requireActivity(), observingSearchedDrinks);
    }

    private void populateOrdersRv(Integer searchedOrder) {
        final int[] proba = {0};
        boolean allOrders = scCafeCurrentOrders.isChecked();

        DatabaseReference cafeCurrentOrdersRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCurrentOrders());
        Query query = null;
        if(searchedOrder != Integer.parseInt(AppConstValue.variableConstValue.ZERO_VALUE)) {
            query = cafeCurrentOrdersRef.orderByChild(firebaseRefPaths.getCurrentOrderTableNumberChild()).equalTo(searchedOrder);
        }
        else if(allOrders) {
            query = cafeCurrentOrdersRef;
        }
        else {
            query = cafeCurrentOrdersRef.orderByChild(firebaseRefPaths.getCurrentOrderDelivererChild()).equalTo(menuViewModel.getPhoneNumber().getValue());
        }
        FirebaseRecyclerOptions<CafeCurrentOrder> currentOrdersOptions = new FirebaseRecyclerOptions
                .Builder<CafeCurrentOrder>()
                .setQuery(query, CafeCurrentOrder.class)
                .build();
        adapterCurrentOrders = new FirebaseRecyclerAdapter<CafeCurrentOrder, CurrentOrderViewHolder>(currentOrdersOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CurrentOrderViewHolder holder, int position, @NonNull CafeCurrentOrder model) {
                DatabaseReference cafeCurrentOrderRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCurrentOrder(getRef(position).getKey()));
                cafeCurrentOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot currentOrderSnapshot) {
                        if(!currentOrderSnapshot.exists()) {
                            return;
                        }
                        Log.d("PROBA123", String.valueOf(proba[0]));
                        proba[0]++;
                        CafeCurrentOrder cafeCurrentOrder = currentOrderSnapshot.getValue(CafeCurrentOrder.class);

                        holder.tvCafeCurrentOrderTable.setText(String.valueOf(cafeCurrentOrder.getCurrentOrderTableNumber()));

                        switch(Objects.requireNonNull(cafeCurrentOrder).getCurrentOrderStatus()) {
                            case order_pending: {
                                holder.clCafeCurrentOrder.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_blue_overlay));
                                holder.tvCafeCurrentOrder.setText(getResources().getString(R.string.pending_orders_order_pending));
                                holder.btnCurrentOrderAction.setText(getResources().getString(R.string.pending_orders_btn_order_pending));
                                holder.btnCurrentOrderAction.setBackgroundColor(getResources().getColor(R.color.red_negative));

                                holder.btnCurrentOrderAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cafeCurrentOrderRef.child(firebaseRefPaths.getCurrentOrderStatusChild()).setValue(
                                                AppConstValue.orderStatusConstValue.ORDER_STATUS_REMOVAL_REQUEST
                                        );
                                    }
                                });

                                break;
                            }
                            case order_ready: {
                                holder.clCafeCurrentOrder.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_green_overlay));
                                holder.tvCafeCurrentOrder.setText(getResources().getString(R.string.pending_orders_order_ready));
                                holder.btnCurrentOrderAction.setText(getResources().getString(R.string.pending_orders_btn_order_ready));
                                holder.btnCurrentOrderAction.setBackgroundColor(getResources().getColor(R.color.green_positive));

                                holder.btnCurrentOrderAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        View orderPaymentView = getLayoutInflater().inflate(R.layout.dialog_order_payment, null);
                                        TextView tvOrderPaymentPrice = (TextView) orderPaymentView.findViewById(R.id.tvOrderPaymentPrice);
                                        TextView tvPaymentIcon = (TextView) orderPaymentView.findViewById(R.id.tvPaymentIcon);
                                        EditText etOrderPaymentReceived = (EditText) orderPaymentView.findViewById(R.id.etOrderPaymentReceived);
                                        etOrderPaymentReceived.setFilters(new InputFilter[] {new DecimalPriceInputFilter(6, 2, 1000000)});
                                        EditText etOrderPaymentReturn = (EditText) orderPaymentView.findViewById(R.id.etOrderPaymentReturn);
                                        etOrderPaymentReturn.setFilters(new InputFilter[] {new DecimalPriceInputFilter(6, 2, 1000000)});
                                        Button btnOrderPaymentConfirm = (Button) orderPaymentView.findViewById(R.id.btnOrderPaymentConfirm);
                                        ImageButton ibCloseOrderPayment = (ImageButton) orderPaymentView.findViewById(R.id.ibCloseOrderPayment);

                                        final AlertDialog orderPaymentDialog = new AlertDialog.Builder(getActivity())
                                                .setView(orderPaymentView)
                                                .create();
                                        orderPaymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        orderPaymentDialog.setCanceledOnTouchOutside(false);
                                        orderPaymentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialogInterface) {

                                                tvOrderPaymentPrice.setText(getResources().getString(R.string.pending_orders_payment_title) +
                                                        AppConstValue.characterConstValue.CHARACTER_SPACING +
                                                        cafeDecimalFormat.format(Float.parseFloat(cafeCurrentOrder.getCurrentOrderTotalPrice())) +
                                                        employeeViewModel.getCafeCurrency().getValue());

                                                etOrderPaymentReceived.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                    }

                                                    @Override
                                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                        if(etOrderPaymentReceived.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                                            etOrderPaymentReceived.setHint(getResources().getString(R.string.pending_orders_payment_received));
                                                            etOrderPaymentReturn.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                                                            etOrderPaymentReturn.setHint(getResources().getString(R.string.pending_orders_payment_return));
                                                            tvPaymentIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_double_arrow_right_32, 0, 0, 0);
                                                        }
                                                        else {
                                                            float receivedPrice = Float.parseFloat(etOrderPaymentReceived.getText().toString());
                                                            float result = receivedPrice - Float.parseFloat(cafeCurrentOrder.getCurrentOrderTotalPrice());
                                                            if(result >= 0f) {
                                                                tvPaymentIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_green_baseline_double_arrow_32, 0, 0, 0);
                                                                etOrderPaymentReturn.setText(cafeDecimalFormat.format(result));
                                                                etOrderPaymentReturn.setTextColor(getResources().getColor(R.color.green_positive));
                                                            }
                                                            else {
                                                                tvPaymentIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_red_baseline_double_arrow_32, 0, 0, 0);
                                                                etOrderPaymentReturn.setText(cafeDecimalFormat.format(result));
                                                                etOrderPaymentReturn.setTextColor(getResources().getColor(R.color.red_negative));
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void afterTextChanged(Editable editable) {

                                                    }
                                                });

                                                btnOrderPaymentConfirm.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        String paymentDateTime = simpleDateFormatLocale.format(new Date());
                                                        CafeBill cafeBill = new CafeBill(
                                                                cafeCurrentOrder.getCurrentOrderDatetime(),
                                                                paymentDateTime,
                                                                cafeCurrentOrder.getCurrentOrderTotalPrice(),
                                                                cafeCurrentOrder.getCurrentOrderDelivererEmployee(),
                                                                cafeCurrentOrder.getCurrentOrderMakerEmployee(),
                                                                cafeCurrentOrder.getCurrentOrderProductAmount(),
                                                                cafeCurrentOrder.getCurrentOrderTableNumber(),
                                                                cafeCurrentOrder.getCurrentOrderDrinks()
                                                        );
                                                        cafeBillsRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeBills()).child(Objects.requireNonNull(currentOrderSnapshot.getKey()));
                                                        cafeBillsRef.setValue(cafeBill);
                                                        cafeCurrentOrderRef.removeValue();

                                                        orderPaymentDialog.dismiss();
                                                    }
                                                });
                                            }
                                        });
                                        ibCloseOrderPayment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                orderPaymentDialog.dismiss();
                                            }
                                        });
                                        orderPaymentDialog.show();
                                    }
                                });

                                break;
                            }
                            case order_declined: {
                                holder.clCafeCurrentOrder.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_purple_overlay));
                                holder.tvCafeCurrentOrder.setText(getResources().getString(R.string.pending_orders_order_declined));
                                holder.btnCurrentOrderAction.setText(getResources().getString(R.string.pending_orders_btn_order_declined));
                                holder.btnCurrentOrderAction.setBackgroundColor(getResources().getColor(R.color.red_negative));

                                holder.btnCurrentOrderAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        View orderMessageView = getLayoutInflater().inflate(R.layout.dialog_order_message, null);
                                        TextView tvCurrentOrderMessage = (TextView) orderMessageView.findViewById(R.id.tvCurrentOrderMessage);
                                        TextView tvCurrentOrderMessageTitle = (TextView) orderMessageView.findViewById(R.id.tvCurrentOrderMessageTitle);
                                        Button btnCurrentOrderRemove = (Button) orderMessageView.findViewById(R.id.btnCurrentOrderRemove);
                                        ImageButton ibCloseOrderMessage = (ImageButton) orderMessageView.findViewById(R.id.ibCloseOrderMessage);

                                        final AlertDialog orderMessageDialog = new AlertDialog.Builder(getActivity())
                                                .setView(orderMessageView)
                                                .create();
                                        orderMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        orderMessageDialog.setCanceledOnTouchOutside(false);
                                        orderMessageDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialogInterface) {
                                                tvCurrentOrderMessageTitle.setText(getResources().getString(R.string.pending_orders_message_title) +
                                                        AppConstValue.characterConstValue.CHARACTER_SPACING + String.valueOf(cafeCurrentOrder.getCurrentOrderTableNumber()));
                                                tvCurrentOrderMessage.setText(cafeCurrentOrder.getCurrentOrderMessage().toString());
                                                btnCurrentOrderRemove.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        cafeCurrentOrderRef.removeValue();
                                                        orderMessageDialog.dismiss();
                                                    }
                                                });

                                                ibCloseOrderMessage.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        orderMessageDialog.dismiss();
                                                    }
                                                });
                                            }
                                        });
                                        orderMessageDialog.show();
                                    }
                                });

                                break;
                            }
                            case order_removal_request: {
                                holder.clCafeCurrentOrder.setBackgroundColor(getResources().getColor(R.color.pager_background_grey));
                                holder.tvCafeCurrentOrder.setText(getResources().getString(R.string.pending_orders_order_removal_request));
                                holder.btnCurrentOrderAction.setText(getResources().getString(R.string.pending_orders_btn_order_removal_request));
                                holder.btnCurrentOrderAction.setBackgroundColor(getResources().getColor(R.color.pewter_blue));

                                holder.btnCurrentOrderAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cafeCurrentOrderRef.child(firebaseRefPaths.getCurrentOrderStatusChild()).setValue(
                                                AppConstValue.orderStatusConstValue.ORDER_STATUS_PENDING
                                        );
                                    }
                                });

                                break;
                            }
                        }

                        holder.btnLookCurrentOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                populateOrderTable(currentOrderSnapshot.getKey(), cafeCurrentOrder);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                        serverAlertDialog.makeAlertDialog();
                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                menuViewModel.getCafeId().getValue(),
                                menuViewModel.getPhoneNumber().getValue(),
                                AppErrorMessage.Title.RETRIEVING_FIREBASE_DATA_FAILED,
                                error.getMessage().toString(),
                                currentDateTime,
                                AppConstValue.errorSender.APP
                        );
                        appError.sendError(appError);
                    }
                });
            }

            @NonNull
            @Override
            public CurrentOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_current_order, parent, false);
                return new CurrentOrderViewHolder(view);
            }
        };
        rvCafeCurrentOrders.setAdapter(adapterCurrentOrders);
        adapterCurrentOrders.startListening();
    }

    private void populateOrderTable(String currentOrderId, CafeCurrentOrder cafeCurrentOrder) {
        View currentOrderTableView = getLayoutInflater().inflate(R.layout.dialog_current_order, null);
        ImageButton ibCloseDialog = currentOrderTableView.findViewById(R.id.ibCloseCurrentOrder);
        TableLayout tlCurrentOrderDrinks = currentOrderTableView.findViewById(R.id.tlCurrentOrderDrinks);
        TableRow trCurrenOrderDrinksTitle = currentOrderTableView.findViewById(R.id.trCurrentOrderDrinkTitle);

        View trTotalView = getLayoutInflater().inflate(R.layout.row_current_order, tlCurrentOrderDrinks, false);
        TableLayout.LayoutParams trTotalViewParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        trTotalViewParams.setMargins(0, 32, 0, 0);

        trTotalView.setLayoutParams(trTotalViewParams);

        switch(cafeCurrentOrder.getCurrentOrderStatus()) {
            case order_pending: {
                trCurrenOrderDrinksTitle.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_blue_overlay));
                trTotalView.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_blue_overlay));
                break;
            }
            case order_ready: {
                trCurrenOrderDrinksTitle.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_green_overlay));
                trTotalView.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_green_overlay));
                break;
            }
            case order_declined: {
                trCurrenOrderDrinksTitle.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_purple_overlay));
                trTotalView.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_purple_overlay));
                break;
            }
            case order_removal_request: {
                trCurrenOrderDrinksTitle.setBackgroundColor(getResources().getColor(R.color.pager_background_grey));
                trTotalView.setBackgroundColor(getResources().getColor(R.color.pager_background_grey));
                break;
            }
        }
        DatabaseReference currentOrderDrinksRef = firebaseDatabase.getReference(firebaseRefPaths.getCurrentOrderDrinks(currentOrderId));
        currentOrderDrinksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot orderDrinksSnapshot) {
                for(DataSnapshot orderDrink : orderDrinksSnapshot.getChildren()) {
                    CafeBillDrink cafeBillDrink = orderDrink.getValue(CafeBillDrink.class);
                    View trDrinkView = getLayoutInflater().inflate(R.layout.row_current_order, tlCurrentOrderDrinks, false);

                    TextView tvCurrentOrderDrinkName = (TextView) trDrinkView.findViewById(R.id.tvCurrentOrderDrinkName);
                    String shortDrinkName = cafeBillDrink.getDrinkName();

                    if (shortDrinkName.length() > AppConstValue.variableConstValue.TABLE_DRINK_NAME_LENGTH) {
                        shortDrinkName = shortDrinkName.substring(0, AppConstValue.variableConstValue.TABLE_DRINK_NAME_LENGTH) + getResources().getString(R.string.etc_dots);
                        tvCurrentOrderDrinkName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toastMessage.showToast(cafeBillDrink.getDrinkName(), 0);
                            }
                        });
                    }
                    tvCurrentOrderDrinkName.setText(shortDrinkName);

                    TextView tvCurrentOrderDrinkAmount = (TextView) trDrinkView.findViewById(R.id.tvCurrentOrderDrinkAmount);
                    tvCurrentOrderDrinkAmount.setText(String.valueOf(cafeBillDrink.getDrinkAmount()));

                    TextView tvCurrentOrderDrinkPrice = (TextView) trDrinkView.findViewById(R.id.tvCurrentOrderDrinkPrice);
                    tvCurrentOrderDrinkPrice.setText(cafeDecimalFormat.format(cafeBillDrink.getDrinkTotalPrice()) +
                            employeeViewModel.getCafeCurrency().getValue());

                    tlCurrentOrderDrinks.addView(trDrinkView);
                }

                TextView tvCurrentOrderDrinkName = (TextView) trTotalView.findViewById(R.id.tvCurrentOrderDrinkName);
                tvCurrentOrderDrinkName.setTypeface(Typeface.DEFAULT_BOLD);
                tvCurrentOrderDrinkName.setText(getResources().getString(R.string.pending_orders_drink_name_total));

                TextView tvCurrentOrderDrinkAmount = (TextView) trTotalView.findViewById(R.id.tvCurrentOrderDrinkAmount);
                tvCurrentOrderDrinkName.setTypeface(Typeface.DEFAULT_BOLD);
                tvCurrentOrderDrinkAmount.setText(String.valueOf(cafeCurrentOrder.getCurrentOrderProductAmount()));

                TextView tvCurrentOrderDrinkPrice = (TextView) trTotalView.findViewById(R.id.tvCurrentOrderDrinkPrice);
                tvCurrentOrderDrinkName.setTypeface(Typeface.DEFAULT_BOLD);
                tvCurrentOrderDrinkPrice.setText(cafeDecimalFormat.format(Float.valueOf(cafeCurrentOrder.getCurrentOrderTotalPrice())) +
                        employeeViewModel.getCafeCurrency().getValue());

                tlCurrentOrderDrinks.addView(trTotalView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessage.Title.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime,
                        AppConstValue.errorSender.APP
                );
                appError.sendError(appError);
            }
        });


        final AlertDialog currentOrderTable = new AlertDialog.Builder(getActivity())
                .setView(currentOrderTableView)
                .create();

        currentOrderTable.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        currentOrderTable.show();

        ibCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentOrderTable.cancel();
            }
        });
    }

    private void stopAdapterListening() {
        if(Objects.nonNull(adapterCurrentOrders)) {
            adapterCurrentOrders.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        stopAdapterListening();
    }
}
