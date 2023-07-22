package com.example.onlinewaiter.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinewaiter.Interfaces.CallBackOrder;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.MenuDrinkViewHolder;
import com.example.onlinewaiter.ViewHolder.OrderDrinkViewHolder;
import com.example.onlinewaiter.employeeUI.order.OrderViewModel;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MenuDrinksAdapter extends RecyclerView.Adapter<MenuDrinkViewHolder> {
    private final Context context;
    private final HashMap<String, CategoryDrink> searchedDrinks;
    private HashMap<String, CafeBillDrink> orderDrinks;
    private final String cafeCurrency;
    private final CallBackOrder callBackOrder;


    public MenuDrinksAdapter (Context context, HashMap<String, CategoryDrink> searchedDrinks, HashMap<String, CafeBillDrink> orderDrinks,
                              String cafeCurrency, CallBackOrder callBackOrder) {
        this.context = context;
        this.searchedDrinks = searchedDrinks;
        this.orderDrinks = orderDrinks;
        this.cafeCurrency = cafeCurrency;
        this.callBackOrder = callBackOrder;
    }

    @NonNull
    @Override
    public MenuDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_drink_item, parent, false);
        return new MenuDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuDrinkViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO);
        int counter = 0;
        for(String searchedDrinkKey : searchedDrinks.keySet()) {
            if(holder.getAbsoluteAdapterPosition() == counter) {
                CategoryDrink categoryDrink = searchedDrinks.get(searchedDrinkKey);
                holder.tvMenuDrinkName.setText(categoryDrink.getCategoryDrinkName());
                holder.tvMenuDrinkDescription.setText(categoryDrink.getCategoryDrinkDescription());
                holder.tvMenuDrinkPrice.setText(decimalFormat.format(categoryDrink.getCategoryDrinkPrice()) + cafeCurrency);
                final int[] drinkAmountCounter = {0};
                holder.tvMenuDrinkAmount.setText(String.valueOf(drinkAmountCounter[0]));
                Glide.with(context).load(categoryDrink.getCategoryDrinkImage()).into(holder.ivMenuDrink);

                String drinkInOrderKey = AppConstValue.variableConstValue.EMPTY_VALUE;
                CafeBillDrink cafeBillDrink;
                if(orderDrinks != null && !orderDrinks.isEmpty()) {
                    for(String orderDrinkKey : orderDrinks.keySet()) {
                        if(searchedDrinkKey.equals(orderDrinkKey)) {
                            drinkAmountCounter[0] = orderDrinks.get(orderDrinkKey).getDrinkAmount();
                            holder.tvMenuDrinkAmount.setText(String.valueOf(drinkAmountCounter[0]));
                            drinkInOrderKey = orderDrinkKey;
                        }
                    }
                }
                else {
                    orderDrinks = new HashMap<>();
                }
                if(drinkInOrderKey.equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                    cafeBillDrink = new CafeBillDrink(
                            searchedDrinkKey,
                            categoryDrink.getCategoryDrinkName(),
                            categoryDrink.getCategoryDrinkImage(),
                            categoryDrink.getCategoryDrinkPrice(),
                            categoryDrink.getCategoryDrinkPrice(),
                            drinkAmountCounter[0]
                            );
                }
                else {
                    cafeBillDrink = orderDrinks.get(searchedDrinkKey);
                }

                holder.btnMenuDrinkAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drinkAmountCounter[0]++;
                        holder.tvMenuDrinkAmount.setText(String.valueOf(drinkAmountCounter[0]));
                        cafeBillDrink.setDrinkAmount((cafeBillDrink.getDrinkAmount() + 1));
                        cafeBillDrink.setDrinkTotalPrice((Float) (cafeBillDrink.getDrinkPrice() * cafeBillDrink.getDrinkAmount()));
                        orderDrinks.put(searchedDrinkKey, cafeBillDrink);
                        callBackOrder.updateOrderDrinks(orderDrinks);
                    }
                });

                holder.btnMenuDrinkRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(drinkAmountCounter[0] > 0) {
                            drinkAmountCounter[0]--;
                            holder.tvMenuDrinkAmount.setText(String.valueOf(drinkAmountCounter[0]));
                            cafeBillDrink.setDrinkAmount((cafeBillDrink.getDrinkAmount() - 1));
                            cafeBillDrink.setDrinkTotalPrice((Float) (cafeBillDrink.getDrinkPrice() * cafeBillDrink.getDrinkAmount()));
                            orderDrinks.put(searchedDrinkKey, cafeBillDrink);
                            callBackOrder.updateOrderDrinks(orderDrinks);
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
        return searchedDrinks.size();
    }
}
