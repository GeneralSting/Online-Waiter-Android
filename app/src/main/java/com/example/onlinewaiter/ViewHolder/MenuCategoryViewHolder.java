package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.Interfaces.ItemClickListener;
import com.example.onlinewaiter.R;

public class MenuCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView ivMenuCategory;
    public TextView tvMenuCategory;
    private ItemClickListener itemClickListener;

    public MenuCategoryViewHolder(View itemView) {
        super(itemView);
        tvMenuCategory = (TextView) itemView.findViewById(R.id.tvMenuCategoryItem);
        ivMenuCategory = (ImageView) itemView.findViewById(R.id.ivMenuCategoryItem);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAbsoluteAdapterPosition(), false);
    }
}
