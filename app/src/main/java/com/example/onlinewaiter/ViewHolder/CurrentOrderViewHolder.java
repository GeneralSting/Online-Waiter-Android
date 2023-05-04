package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.R;

public class CurrentOrderViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout clCafeCurrentOrder;
    public TextView tvCafeCurrentOrder, tvCafeCurrentOrderTable;
    public Button btnLookCurrentOrder, btnCurrentOrderAction;

    public CurrentOrderViewHolder(View itemView) {
        super(itemView);

        clCafeCurrentOrder = (ConstraintLayout) itemView.findViewById(R.id.clCafeCurrentOrder);
        tvCafeCurrentOrderTable = (TextView) itemView.findViewById(R.id.tvCafeCurrentOrderTable);
        tvCafeCurrentOrder = (TextView) itemView.findViewById(R.id.tvCafeCurrentOrder);
        btnLookCurrentOrder = (Button) itemView.findViewById(R.id.btnLookCurrentOrder);
        btnCurrentOrderAction = (Button) itemView.findViewById(R.id.btnCurrentOrderAction);
    }
}
