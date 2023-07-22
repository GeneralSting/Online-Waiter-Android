package com.example.onlinewaiter.Adapter;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinewaiter.Models.ViewPagerItem;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.ViewMargins;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.ViewPagerItemHolder;

import java.util.ArrayList;
import java.util.Arrays;

public class AppPagerAdapter extends RecyclerView.Adapter<ViewPagerItemHolder> {

    ArrayList<ViewPagerItem> viewPagerItems;
    private final Context context;

    public AppPagerAdapter(Context context, ArrayList<ViewPagerItem> viewPagerItems) {
        this.context = context;
        this.viewPagerItems = viewPagerItems;
    }

    @NonNull
    @Override
    public ViewPagerItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewPagerItem = LayoutInflater.from(context)
                .inflate(R.layout.main_pager_item, parent, false);
        return new ViewPagerItemHolder(viewPagerItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerItemHolder holder, int position) {
        ViewPagerItem viewPagerItem = viewPagerItems.get(position);
        Glide.with(context).load(viewPagerItem.getImageId()).centerCrop().into(holder.ivPagerItemImage);
        if(!viewPagerItem.isShowScrollIcon()) {
            holder.ivScrollPager.setVisibility(View.GONE);
        }
        else {
            holder.txtPagerItemDescription.setTextSize(18);
            ViewMargins.setMargins(holder.txtPagerItemDescription, 0, 32, 0, 0);
        }
        holder.txtPagerItemHeader.setText(viewPagerItem.getHeader());
        if(viewPagerItem.getDescription().length() > AppConstValue.variableConstValue.MAIN_PAGER_DESCRIPTION_END) {
            viewPagerItem.setDescription(viewPagerItem.getDescription().substring(
                    AppConstValue.variableConstValue.MAIN_PAGER_DESCRIPTION_START,
                    AppConstValue.variableConstValue.MAIN_PAGER_DESCRIPTION_END));
            ArrayList<String> splitedWords = new ArrayList<String>(Arrays.asList(viewPagerItem.getDescription().split(AppConstValue.regex.WHITESPACE_CHARACHTERS)));
            splitedWords.remove(splitedWords.size() - 1);
            viewPagerItem.setDescription(splitedWords.toString()
                    .replace(AppConstValue.variableConstValue.COMMA, AppConstValue.variableConstValue.EMPTY_VALUE)  //remove the commas
                    .replace(AppConstValue.variableConstValue.SQUARE_BRACKETS_OPEN, AppConstValue.variableConstValue.EMPTY_VALUE)  //remove the right bracket
                    .replace(AppConstValue.variableConstValue.SQUARE_BRACKETS_CLOSED, AppConstValue.variableConstValue.EMPTY_VALUE)  //remove the left bracket
                    .trim() + context.getResources().getString(R.string.etc_dots));           //remove trailing spaces from partially initialized arrays);
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
        return viewPagerItems != null ? viewPagerItems.size() : 0;
    }
}
