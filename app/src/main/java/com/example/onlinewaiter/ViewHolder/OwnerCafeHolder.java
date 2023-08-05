package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.R;

public class OwnerCafeHolder extends RecyclerView.ViewHolder {
    public TextView tvOwnerCafeName, tvOwnerCafeCountry, tvOwnerCafeLocation;
    public ConstraintLayout clOwnerCafe;

    public OwnerCafeHolder(View itemView) {
        super(itemView);

        tvOwnerCafeName = (TextView) itemView.findViewById(R.id.tvOwnerCafeName);
        tvOwnerCafeCountry = (TextView) itemView.findViewById(R.id.tvOwnerCafeCountry);
        tvOwnerCafeLocation = (TextView) itemView.findViewById(R.id.tvOwnerCafeLocation);
        clOwnerCafe = (ConstraintLayout) itemView.findViewById(R.id.clOwnerCafe);
    }
}
