package com.example.onlinewaiter.ownerUI.statistics;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentCafeUpdateBinding;
import com.example.onlinewaiter.databinding.FragmentMenuBinding;
import com.example.onlinewaiter.databinding.FragmentStatisticsBinding;

public class StatisticsFragment extends Fragment {

    //global variables/objects
    private FragmentStatisticsBinding binding;

    //fragment views


    //firebase

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}