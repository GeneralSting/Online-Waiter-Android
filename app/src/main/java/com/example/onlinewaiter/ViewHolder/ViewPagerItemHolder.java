package com.example.onlinewaiter.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.R;

public class ViewPagerItemHolder extends RecyclerView.ViewHolder {

    public ImageView ivPagerItemImage;
    public TextView txtPagerItemHeader, txtPagerItemDescription, txtPagerItemMoreDescription;
    public ViewPagerItemHolder(@NonNull View itemView) {
        super(itemView);

        ivPagerItemImage = itemView.findViewById(R.id.ivMainPagerItemImage);
        txtPagerItemHeader = itemView.findViewById(R.id.txtMainPagerItemHeader);
        txtPagerItemDescription = itemView.findViewById(R.id.txtMainPagerItemDecription);
        txtPagerItemMoreDescription = itemView.findViewById(R.id.txtMainPagerMoreDescription);
    }
}
