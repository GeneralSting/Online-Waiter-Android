package com.example.onlinewaiter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinewaiter.Interfaces.CallBackOrder;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.OrderDrinkViewHolder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDrinksAdapter extends RecyclerView.Adapter<OrderDrinkViewHolder> {
    Context context;
    HashMap<String, CafeBillDrink> orderDrinks;
    CallBackOrder callBackOrder;

    public OrderDrinksAdapter (Context context, HashMap<String, CafeBillDrink> orderDrinks, CallBackOrder callBackOrder) {
        this.context = context;
        this.orderDrinks = orderDrinks;
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
        String countryCurrency = context.getResources().getString(R.string.country_currency);
        int counter = 0;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        HashMap<String, CafeBillDrink> newOrderDrinks = new HashMap<>();
        for(Map.Entry<String, CafeBillDrink> entry : orderDrinks.entrySet()) {
            if(holder.getAbsoluteAdapterPosition() == counter) {
                CafeBillDrink cafeBillDrink = entry.getValue();
                holder.tvOrderDrinkName.setText(cafeBillDrink.getDrinkName());
                holder.tvOrderDrinkPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkPrice()) +
                        context.getResources().getString(R.string.country_currency));
                holder.tvOrderDrinkTotalPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkTotalPrice()) + countryCurrency);
                holder.tvOrderDrinkAmount.setText(String.valueOf(cafeBillDrink.getDrinkAmount()));
                Glide.with(context).load(cafeBillDrink.getDrinkImage()).into(holder.ivOrderDrinkImage);

                holder.btnOrderDrinkAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cafeBillDrink.setDrinkAmount((cafeBillDrink.getDrinkAmount() + 1));
                        cafeBillDrink.setDrinkTotalPrice((Float) (cafeBillDrink.getDrinkPrice() * cafeBillDrink.getDrinkAmount()));
                        holder.tvOrderDrinkTotalPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkTotalPrice()) + countryCurrency);
                        holder.tvOrderDrinkAmount.setText(String.valueOf(cafeBillDrink.getDrinkAmount()));
                        orderDrinks.put(cafeBillDrink.getDrinkId(), cafeBillDrink);
                        callBackOrder.updateOrderDrinks(orderDrinks);
                    }
                });

                holder.btnOrderDrinkRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(cafeBillDrink.getDrinkAmount() > 0) {
                            cafeBillDrink.setDrinkAmount((cafeBillDrink.getDrinkAmount()) - 1);
                            if(cafeBillDrink.getDrinkAmount() > 0) {
                                cafeBillDrink.setDrinkTotalPrice((Float) (cafeBillDrink.getDrinkPrice() * cafeBillDrink.getDrinkAmount()));
                                holder.tvOrderDrinkTotalPrice.setText(decimalFormat.format(cafeBillDrink.getDrinkTotalPrice()) + countryCurrency);
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
