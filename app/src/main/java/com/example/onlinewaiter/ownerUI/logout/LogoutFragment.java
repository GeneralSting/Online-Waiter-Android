package com.example.onlinewaiter.ownerUI.logout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentCafeUpdateBinding;
import com.example.onlinewaiter.databinding.FragmentLogoutBinding;
import com.example.onlinewaiter.databinding.FragmentMenuBinding;

public class LogoutFragment extends Fragment {

    //global variables/objects
    private FragmentLogoutBinding binding;


    //fragment views


    //firebase


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}