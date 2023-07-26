package com.example.onlinewaiter.ownerUI.main;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.Cafe;
import com.example.onlinewaiter.Models.CafeDrinksCategory;
import com.example.onlinewaiter.Models.CafeEmployee;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MainFragment extends Fragment {
    //fragment views
    private TextView tvMainCafeName, tvMainTables, tvMainEmployees, tvMainCategories, tvMainDrinks;
    private CardView cvMainTables, cvMainEmployees, cvMainCategories, cvMainDrinks;
    private TableLayout tlOwnerMainEmployees;


    //global variables/objects
    private FragmentMainBinding binding;
    private MainViewModel mainViewModel;
    private ToastMessage toastMessage;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private AppError appError;


    //firebase
    private FirebaseRefPaths firebaseRefPaths;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        toastMessage = new ToastMessage(getActivity());
        firebaseRefPaths = new FirebaseRefPaths();
        tvMainCafeName = binding.tvOwnerMainCafeName;
        tvMainTables = binding.tvOwnerMainTables;
        tvMainEmployees = binding.tvOwnerMainEmployees;
        tvMainCategories = binding.tvOwnerMainCategories;
        tvMainDrinks = binding.tvOwnerMainDrinks;
        cvMainTables = binding.cvOwnerMainTables;
        cvMainEmployees = binding.cvOwnerMainEmployees;
        cvMainCategories = binding.cvOwnerMainCategories;
        cvMainDrinks = binding.cvOwnerMainDrinks;
        tlOwnerMainEmployees = binding.tlOwnerMainEmployees;

        cvClickMessage();
        mainViewModelObservers();

        return root;
    }

    private void cvClickMessage() {
        cvMainTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.main_cv_tables), 0);
            }
        });

        cvMainEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.main_cv_employees), 0);
            }
        });

        cvMainCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.main_cv_categories), 0);
            }
        });

        cvMainDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.main_cv_drinks), 0);
            }
        });
    }

    private void mainViewModelObservers() {
        final Observer<String> ObservingCafeId = new Observer<String>() {
            @Override
            public void onChanged(String ownerCafeId) {
                cafeDetails(ownerCafeId);
            }
        };
        mainViewModel.getOwnerCafeId().observe(requireActivity(), ObservingCafeId);

        super.onResume();
        final Observer<Integer> ObservingCafeInfo = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                cafeDetails(mainViewModel.getOwnerCafeId().getValue());
            }
        };
        mainViewModel.getUpdateInfo().observe(requireActivity(), ObservingCafeInfo);
    }

    private void cafeDetails(String cafeId) {
        //firebase
        DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getCafeOwner(cafeId));
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeSnapshot) {
                if(!cafeSnapshot.exists()) {
                    return;
                }
                int cafeTotalDrinks = 0;
                Cafe cafe = cafeSnapshot.getValue(Cafe.class);
                tvMainCafeName.setText(cafe.getCafeName());
                tvMainCafeName.setPaintFlags(tvMainCafeName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tvMainTables.setText(String.valueOf(cafe.getCafeTables()));
                tvMainEmployees.setText(String.valueOf(cafe.getCafeEmployees().size()));
                tvMainCategories.setText(String.valueOf(cafe.getCafeDrinksCategories().size()));

                for(Map.Entry<String, CafeDrinksCategory> cafeDrinksCategoryEntry : cafe.getCafeDrinksCategories().entrySet()) {
                    CafeDrinksCategory cafeDrinksCategory = cafeDrinksCategoryEntry.getValue();
                    cafeTotalDrinks += cafeDrinksCategory.getCategoryDrinks().size();
                }
                tvMainDrinks.setText(String.valueOf(cafeTotalDrinks));

                while(tlOwnerMainEmployees.getChildCount() > 0) {
                    tlOwnerMainEmployees.removeView(tlOwnerMainEmployees.getChildAt(tlOwnerMainEmployees.getChildCount() - 1));
                }
                for(CafeEmployee cafeEmployee : cafe.getCafeEmployees().values()) {
                    View trCafeEmployeeView = getLayoutInflater().inflate(R.layout.cafe_employee_row, tlOwnerMainEmployees, false);

                    TextView tvCafeEmployeeName = (TextView) trCafeEmployeeView.findViewById(R.id.tvCafeEmployeeName);
                    tvCafeEmployeeName.setText(cafeEmployee.getEmployeeName());

                    TextView tvCafeEmployeeLastname = (TextView) trCafeEmployeeView.findViewById(R.id.tvCafeEmployeeLastname);
                    tvCafeEmployeeLastname.setText(cafeEmployee.getEmployeeLastname());

                    TextView tvCafeEmployeeBirthDate = (TextView) trCafeEmployeeView.findViewById(R.id.tvCafeEmployeeBirthDate);
                    tvCafeEmployeeBirthDate.setText(cafeEmployee.getEmployeeBirthDate());

                    TextView tvCafeEmployeeGender = (TextView) trCafeEmployeeView.findViewById(R.id.tvCafeEmployeeGender);
                    tvCafeEmployeeGender.setText(cafeEmployee.getEmployeeGender());

                    TextView tvCafeEmployeeGmail = (TextView) trCafeEmployeeView.findViewById(R.id.tvCafeEmployeeGmail);
                    tvCafeEmployeeGmail.setText(cafeEmployee.getEmployeeGmail());

                    tlOwnerMainEmployees.addView(trCafeEmployeeView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}