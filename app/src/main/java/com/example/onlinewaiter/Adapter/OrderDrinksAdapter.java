package com.example.onlinewaiter.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinewaiter.Interfaces.CallBackOrder;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.OrderDrinkViewHolder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDrinksAdapter extends RecyclerView.Adapter<OrderDrinkViewHolder> {
    private final Context context;
    private final HashMap<String, CafeBillDrink> orderDrinks;
    private final String cafeCurrency;
    private final CallBackOrder callBackOrder;

    public OrderDrinksAdapter (Context context, HashMap<String, CafeBillDrink> orderDrinks, String cafeCurrency, CallBackOrder callBackOrder) {
        this.context = context;
        this.orderDrinks = orderDrinks;
        this.cafeCurrency = cafeCurrency;
        this.callBackOrder = callBackOrder;
    }

    @NonNull
    @Override
    public OrderDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_drink_item, parent, false);
        return new OrderDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDrinkViewHolder holder, int position) {
        int counter = 0;
        DecimalFormat decimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO);
        HashMap<String, CafeBillDrink> newOrderDrinks = new HashMap<>();
        for(String key : orderDrinks.keySet()) {
            if(holder.getAbsoluteAdapterPosition() == counter) {
                CafeBillDrink cafeBillDrink = orderDrinks.get(key);
                holder.tvOrderDrinkName.setText(cafeBillDrink.getDrinkName());
                holder.tvOrderDrinkPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkPrice()) + cafeCurrency);
                holder.tvOrderDrinkTotalPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkTotalPrice()) + cafeCurrency);
                holder.tvOrderDrinkAmount.setText(String.valueOf(cafeBillDrink.getDrinkAmount()));
                Glide.with(context).load(cafeBillDrink.getDrinkImage()).into(holder.ivOrderDrinkImage);

                holder.btnOrderDrinkAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(cafeBillDrink.getDrinkAmount() < cafeBillDrink.getDrinkQuantity()) {
                            cafeBillDrink.setDrinkAmount((cafeBillDrink.getDrinkAmount() + 1));
                            cafeBillDrink.setDrinkTotalPrice((Float) (cafeBillDrink.getDrinkPrice() * cafeBillDrink.getDrinkAmount()));
                            holder.tvOrderDrinkTotalPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkTotalPrice()) + cafeCurrency);
                            holder.tvOrderDrinkAmount.setText(String.valueOf(cafeBillDrink.getDrinkAmount()));
                            orderDrinks.put(cafeBillDrink.getDrinkId(), cafeBillDrink);
                            callBackOrder.updateOrderDrinks(orderDrinks);
                        }
                        else {
                            ToastMessage toastMessage = new ToastMessage(context);
                            toastMessage.showToast(context.getResources().getString(R.string.main_drink_unavailable), 0);
                        }
                    }
                });

                holder.btnOrderDrinkRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(cafeBillDrink.getDrinkAmount() > 0) {
                            cafeBillDrink.setDrinkAmount((cafeBillDrink.getDrinkAmount()) - 1);
                            if(cafeBillDrink.getDrinkAmount() > 0) {
                                cafeBillDrink.setDrinkTotalPrice((Float) (cafeBillDrink.getDrinkPrice() * cafeBillDrink.getDrinkAmount()));
                                holder.tvOrderDrinkTotalPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkTotalPrice()) + cafeCurrency);
                                holder.tvOrderDrinkAmount.setText(String.valueOf(cafeBillDrink.getDrinkAmount()));
                                orderDrinks.put(cafeBillDrink.getDrinkId(), cafeBillDrink);
                                callBackOrder.updateOrderDrinks(orderDrinks);
                            }
                            else {
                                orderDrinks.remove(cafeBillDrink.getDrinkId());
                                callBackOrder.updateOrderDrinks(orderDrinks);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, orderDrinks.size());
                            }
                        }
                    }
                });
                break;
            }
            counter++;
        }
    }

    @Override
    public int getItemCount() {
        return orderDrinks.size();
    }
}
