package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.Interfaces.ItemClickListener;
import com.example.onlinewaiter.R;

public class UpdateDrinkViewHolder extends RecyclerView.ViewHolder {
    public EditText etUpdateDrinkName, etUpdateDrinkDescription, etUpdateDrinkPrice, etUpdateDrinkQuantity;
    public TextView tvDrinkPriceCurrency;
    public ImageView ivUpdateDrinkComparison, ivUpdateDrink;
    public ImageButton btnUpdateDrinkAccept, btnUpdateDrinkRemove;

    public UpdateDrinkViewHolder(View itemView) {
        super(itemView);

        etUpdateDrinkName = (EditText) itemView.findViewById(R.id.etUpdateDrinkName);
        etUpdateDrinkDescription = (EditText) itemView.findViewById(R.id.etUpdateDrinkDescription);
        etUpdateDrinkPrice = (EditText) itemView.findViewById(R.id.etUpdateDrinkPrice);
        etUpdateDrinkQuantity = (EditText) itemView.findViewById(R.id.etUpdateDrinkQuantity);
        tvDrinkPriceCurrency = (TextView) itemView.findViewById(R.id.tvDrinkPriceCurrency);
        ivUpdateDrinkComparison = (ImageView) itemView.findViewById(R.id.ivUpdateDrinkComparison);
        ivUpdateDrink = (ImageView) itemView.findViewById(R.id.ivUpdateDrink);
        btnUpdateDrinkAccept = (ImageButton) itemView.findViewById(R.id.btnUpdateDrinkAccept);
        btnUpdateDrinkRemove = (ImageButton) itemView.findViewById(R.id.btnUpdateDrinkRemove);
    }
}
