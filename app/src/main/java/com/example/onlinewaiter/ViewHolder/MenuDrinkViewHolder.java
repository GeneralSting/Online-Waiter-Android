package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.R;

public class MenuDrinkViewHolder extends RecyclerView.ViewHolder {
    public TextView tvMenuDrinkName, tvMenuDrinkDescription, tvMenuDrinkPrice, tvMenuDrinkAmount, tvMenuDrinkQuantity;
    public ImageView ivMenuDrink;
    public Button btnMenuDrinkAdd, btnMenuDrinkRemove;

    public MenuDrinkViewHolder(View itemView) {
        super(itemView);

        tvMenuDrinkName = (TextView) itemView.findViewById(R.id.tvMenuDrinkItemName);
        tvMenuDrinkDescription = (TextView) itemView.findViewById(R.id.tvMenuDrinkItemDescription);
        tvMenuDrinkPrice = (TextView) itemView.findViewById(R.id.tvMenuDrinkItemPrice);
        tvMenuDrinkAmount = (TextView) itemView.findViewById(R.id.tvMenuDrinkItemAmount);
        tvMenuDrinkQuantity = (TextView) itemView.findViewById(R.id.tvMenuDrinkQuantity);
        ivMenuDrink = (ImageView) itemView.findViewById(R.id.ivMenuDrinkItem);
        btnMenuDrinkAdd = (Button) itemView.findViewById(R.id.btnMenuDrinkItemAdd);
        btnMenuDrinkRemove = (Button) itemView.findViewById(R.id.btnMenuDrinkItemRemove);
    }
}
