package com.example.onlinewaiter.ownerUI.registeredNumbers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentCafeUpdateBinding;
import com.example.onlinewaiter.databinding.FragmentMenuBinding;
import com.example.onlinewaiter.databinding.FragmentRegisteredNumbersBinding;

public class RegisteredNumbersFragment extends Fragment {

    //global variables/objects
    private FragmentRegisteredNumbersBinding binding;


    //fragment views

    //firebase

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisteredNumbersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}