package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.R;

public class OrderDrinkViewHolder  extends RecyclerView.ViewHolder {
    public TextView tvOrderDrinkName, tvOrderDrinkPrice, tvOrderDrinkTotalPrice, tvOrderDrinkAmount;
    public ImageView ivOrderDrinkImage;
    public Button btnOrderDrinkAdd, btnOrderDrinkRemove;

    public OrderDrinkViewHolder(View itemView) {
        super(itemView);

        tvOrderDrinkName = (TextView) itemView.findViewById(R.id.tvOrderDrinkName);
        tvOrderDrinkPrice = (TextView) itemView.findViewById(R.id.tvOrderDrinkPrice);
        tvOrderDrinkTotalPrice = (TextView) itemView.findViewById(R.id.tvOrderDrinkTotalPrice);
        tvOrderDrinkAmount = (TextView) itemView.findViewById(R.id.tvOrderDrinkAmount);
        ivOrderDrinkImage = (ImageView) itemView.findViewById(R.id.ivOrderDrinkImage);
        btnOrderDrinkAdd = (Button) itemView.findViewById(R.id.btnOrderDrinkAdd);
        btnOrderDrinkRemove = (Button) itemView.findViewById(R.id.btnOrderDrinkRemove);
    }
}
