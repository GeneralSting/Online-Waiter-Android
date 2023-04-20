package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.Interfaces.ItemClickListener;
import com.example.onlinewaiter.R;

public class UpdateDrinkViewHolder extends RecyclerView.ViewHolder {
    public EditText etUpdateDrinkName, etUpdateDrinkDescription, etUpdateDrinkPrice;
    public ImageView ivUpdateDrinkComparison, ivUpdateDrink;
    public ImageButton btnUpdateDrinkAccept, btnUpdateDrinkRemove;

    private ItemClickListener itemClickListener;

    public UpdateDrinkViewHolder(View itemView) {
        super(itemView);

        etUpdateDrinkName = (EditText) itemView.findViewById(R.id.etUpdateDrinkName);
        etUpdateDrinkDescription = (EditText) itemView.findViewById(R.id.etUpdateDrinkDescription);
        etUpdateDrinkPrice = (EditText) itemView.findViewById(R.id.etUpdateDrinkPrice);
        ivUpdateDrinkComparison = (ImageView) itemView.findViewById(R.id.ivUpdateDrinkComparison);
        ivUpdateDrink = (ImageView) itemView.findViewById(R.id.ivUpdateDrink);
        btnUpdateDrinkAccept = (ImageButton) itemView.findViewById(R.id.btnUpdateDrinkAccept);
        btnUpdateDrinkRemove = (ImageButton) itemView.findViewById(R.id.btnUpdateDrinkRemove);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
