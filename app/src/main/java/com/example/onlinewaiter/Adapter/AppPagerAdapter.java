package com.example.onlinewaiter.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinewaiter.MainActivity;
import com.example.onlinewaiter.Models.ViewPagerItem;
import com.example.onlinewaiter.Other.TextSpan;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.ViewPagerItemHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class AppPagerAdapter extends RecyclerView.Adapter<ViewPagerItemHolder> {

    ArrayList<ViewPagerItem> viewPagerItems;
    private Context context;

    public AppPagerAdapter(ArrayList<ViewPagerItem> viewPagerItems) {
        this.viewPagerItems = viewPagerItems;
    }

    @NonNull
    @Override
    public ViewPagerItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View viewPagerItem = LayoutInflater.from(context)
                .inflate(R.layout.main_pager_item, parent, false);
        return new ViewPagerItemHolder(viewPagerItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerItemHolder holder, int position) {
        ViewPagerItem viewPagerItem = viewPagerItems.get(position);
        Glide.with(context).load(viewPagerItem.getImageID()).centerCrop().into(holder.ivPagerItemImage);
        holder.txtPagerItemHeader.setText(viewPagerItem.getHeader());
        if(viewPagerItem.getDescription().length() > 150) {
            viewPagerItem.setDescription(viewPagerItem.getDescription().substring(0, 150));
            ArrayList<String> splitedWords = new ArrayList<String>(Arrays.asList(viewPagerItem.getDescription().split("\\s+")));
            splitedWords.remove(splitedWords.size() - 1);
            viewPagerItem.setDescription(splitedWords.toString()
                    .replace(",", "")  //remove the commas
                    .replace("[", "")  //remove the right bracket
                    .replace("]", "")  //remove the left bracket
                    .trim() + "...");           //remove trailing spaces from partially initialized arrays);
            holder.txtPagerItemMoreDescription.setVisibility(View.VISIBLE);
            holder.txtPagerItemMoreDescription.setMovementMethod(LinkMovementMethod.getInstance());
            holder.txtPagerItemDescription.setText(viewPagerItem.getDescription());
        }
        else {
            holder.txtPagerItemDescription.setText(viewPagerItem.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return viewPagerItems.size();
    }
}
