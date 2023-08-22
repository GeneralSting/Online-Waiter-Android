package com.example.onlinewaiter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.onlinewaiter.Models.RegisteredCountry;
import com.example.onlinewaiter.R;

import java.util.ArrayList;

public class CountryItemAdapter extends ArrayAdapter<RegisteredCountry> {

    public CountryItemAdapter(Context context, ArrayList<RegisteredCountry> registeredCountries, int tvCountryItemNameId) {
        super(context, tvCountryItemNameId, registeredCountries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country, parent, false);
        }

        ImageView ivCountryItemFlag = convertView.findViewById(R.id.ivCountryItemFlag);
        TextView tvCountryItemName = convertView.findViewById(R.id.tvCountryItemName);
        TextView tvCountryItemCode = convertView.findViewById(R.id.tvCountryItemCode);

        RegisteredCountry registeredCountry = getItem(position);
        if(registeredCountry != null) {
            Glide.with(getContext()).load(registeredCountry.getFlag()).into(ivCountryItemFlag);
            tvCountryItemName.setText(registeredCountry.getName());
            tvCountryItemCode.setText(registeredCountry.getCode());
        }

        return convertView;
    }


}
