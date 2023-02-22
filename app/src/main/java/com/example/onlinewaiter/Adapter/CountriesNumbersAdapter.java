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
import com.example.onlinewaiter.Models.CountryNumber;
import com.example.onlinewaiter.R;

import java.util.List;

public class CountriesNumbersAdapter extends ArrayAdapter<CountryNumber> {

    private static class CountriesNumberViewHolder {
        TextView txtCountryName, txtCountryNumberCode;
        ImageView ivCountryFlag;
    }

    private static class CountryFlagViewHolder {
        ImageView ivCountryFlag;
    }

    LayoutInflater layoutInflater;
    public CountriesNumbersAdapter(@NonNull Context context, int resource, @NonNull List<CountryNumber> countryNumbers) {
        super(context, resource, countryNumbers);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CountriesNumberViewHolder countriesNumberViewHolder;
        if(convertView == null) {
            countriesNumberViewHolder = new CountriesNumberViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_number_dropdown_item, parent, false);
            CountryNumber countryNumber = getItem(position);
            countriesNumberViewHolder.txtCountryName = (TextView) convertView.findViewById(R.id.txtDropDownItemName);
            countriesNumberViewHolder.txtCountryName.setText(countryNumber.getCountryName());
            countriesNumberViewHolder.txtCountryNumberCode = (TextView) convertView.findViewById(R.id.txtDropDownItemCode);
            countriesNumberViewHolder.txtCountryNumberCode.setText("(" + countryNumber.getCountryNumberCode() + ")");
            countriesNumberViewHolder.ivCountryFlag = (ImageView) convertView.findViewById(R.id.ivDropdownItemImage);
            Glide.with(parent.getContext()).load(countryNumber.getCountryFlag()).into(countriesNumberViewHolder.ivCountryFlag);

            convertView.setTag(countriesNumberViewHolder);
        }
        else {
            countriesNumberViewHolder = (CountriesNumberViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CountryFlagViewHolder countryFlagViewHolder;
        if(convertView == null) {
            countryFlagViewHolder = new CountryFlagViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_number_item, parent, false);
            CountryNumber countryNumber = getItem(position);
            countryFlagViewHolder.ivCountryFlag = (ImageView) convertView.findViewById(R.id.ivLoginCountryFlag);
            Glide.with(parent.getContext()).load(countryNumber.getCountryFlag()).into(countryFlagViewHolder.ivCountryFlag);

            convertView.setTag(countryFlagViewHolder);
        }
        else {
            countryFlagViewHolder = (CountryFlagViewHolder) convertView.getTag();
        }
        return convertView;
    }
}
