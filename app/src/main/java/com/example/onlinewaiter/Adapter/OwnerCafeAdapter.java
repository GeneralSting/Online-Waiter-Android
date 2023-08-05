package com.example.onlinewaiter.Adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinewaiter.Interfaces.OwnerCafeClick;
import com.example.onlinewaiter.Models.Cafe;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.OwnerCafeHolder;
import java.util.HashMap;

public class OwnerCafeAdapter extends RecyclerView.Adapter<OwnerCafeHolder> {
    private final HashMap<String, Cafe> ownerCafes;
    private Context context;
    private final OwnerCafeClick ownerCafeClick;

    public OwnerCafeAdapter (HashMap<String, Cafe> ownerCafes, Context context, OwnerCafeClick ownerCafeClick) {
        this.ownerCafes = ownerCafes;
        this.context = context;
        this.ownerCafeClick = ownerCafeClick;
    }

    @NonNull
    @Override
    public OwnerCafeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_owner_cafe, parent, false);
        return new OwnerCafeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerCafeHolder holder, int position) {
        int counter = 0;
        for(String cafeId : ownerCafes.keySet()) {
            if(holder.getAbsoluteAdapterPosition() == counter) {
                Cafe cafe = ownerCafes.get(cafeId);
                holder.tvOwnerCafeName.setText(cafe.getCafeName());
                holder.tvOwnerCafeCountry.setText(cafe.getCafeCountry());
                holder.tvOwnerCafeLocation.setText(cafe.getCafeLocation());
                holder.clOwnerCafe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ownerCafeClick.selectedOwnerCafe(cafeId, true);
                    }
                });
            }
            counter++;
        }
    }

    @Override
    public int getItemCount() {
        return ownerCafes.size();
    }
}
