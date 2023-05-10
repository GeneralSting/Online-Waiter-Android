package com.example.onlinewaiter.ownerUI.logout;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.onlinewaiter.MainActivity;
import com.example.onlinewaiter.OwnerActivity;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentCafeUpdateBinding;
import com.example.onlinewaiter.databinding.FragmentLogoutBinding;
import com.example.onlinewaiter.databinding.FragmentMenuBinding;
import com.example.onlinewaiter.ownerUI.registeredNumbers.RegisteredNumbersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LogoutFragment extends Fragment {

    //global variables/objects
    private FragmentLogoutBinding binding;
    private RegisteredNumbersViewModel registeredNumbersViewModel;


    //fragment views
    Button btnOwnerLogout;
    TextView tvOwnerPhoneChanged;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnOwnerLogout = binding.btnOwnerLogout;
        btnOwnerLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser != null) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                }
                if(currentUser == null) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                }
            }
        });
        registeredNumbersViewModel = new ViewModelProvider(requireActivity()).get(RegisteredNumbersViewModel.class);
        if(Boolean.TRUE.equals(registeredNumbersViewModel.getPhoneNumberChanged().getValue())) {
            tvOwnerPhoneChanged = binding.tvOwnerPhoneChanged;
            tvOwnerPhoneChanged.setText(getResources().getString(R.string.logout_phone_number_changed));
        }
        return root;
    }
}