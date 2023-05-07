package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.R;

public class RegisteredNumberViewHolder extends RecyclerView.ViewHolder {
    public CardView cvRegisteredNumber;
    public ConstraintLayout clRegisteredNumber;
    public TextView tvRegisteredNumber;
    public Button btnRegisteredNumberDisable, btnRegisteredNumberRemove, btnRegisteredNumberOwner;

    public RegisteredNumberViewHolder(View itemView) {
        super(itemView);

        cvRegisteredNumber = itemView.findViewById(R.id.cvRegisteredNumber);
        clRegisteredNumber = itemView.findViewById(R.id.clRegisteredNumber);
        tvRegisteredNumber = itemView.findViewById(R.id.tvRegisteredNumber);
        btnRegisteredNumberDisable = itemView.findViewById(R.id.btnRegisteredNumberDisable);
        btnRegisteredNumberRemove = itemView.findViewById(R.id.btnRegisteredNumberRemove);
        btnRegisteredNumberOwner = itemView.findViewById(R.id.btnRegisterNumberOwner);
    }
}
