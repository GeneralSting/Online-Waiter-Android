package com.example.onlinewaiter.employeeUI.pendingOrders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.onlinewaiter.databinding.FragmentOrderBinding;
import com.example.onlinewaiter.databinding.FragmentPendingOrdersBinding;

public class PendingOrdersFragment extends Fragment {
    private FragmentPendingOrdersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPendingOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
}
