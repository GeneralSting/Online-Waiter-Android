package com.example.onlinewaiter.ownerUI.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.onlinewaiter.Adapter.CountryItemAdapter;
import com.example.onlinewaiter.Functions.EmailValidator;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.Cafe;
import com.example.onlinewaiter.Models.CafeDrinksCategory;
import com.example.onlinewaiter.Models.RegisteredCountry;
import com.example.onlinewaiter.Models.RegisteredNumber;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessage;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.databinding.FragmentMainBinding;
import com.example.onlinewaiter.ownerUI.GlobalViewModel.OwnerViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainFragment extends Fragment {
    //fragment views
    private TextView tvMainTables, tvMainNumbers, tvMainCategories, tvMainDrinks, tvMainCafeName, tvMainCafeLocation, tvMainCafeCountry, tvMainCafeEmail;
    private CardView cvMainCafeName, cvMainCafeLocation, cvMainCafeCountry, cvMainCafeEmail;
    private CardView cvMainTables, cvMainEmployees, cvMainCategories, cvMainDrinks;


    //global variables/objects
    private FragmentMainBinding binding;
    private String selectedCodeLocale;
    private MainViewModel mainViewModel;
    private ToastMessage toastMessage;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private AppError appError;


    //firebase
    private FirebaseRefPaths firebaseRefPaths;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        toastMessage = new ToastMessage(getActivity());
        firebaseRefPaths = new FirebaseRefPaths();
        cvMainCafeName = binding.cvMainCafeName;
        cvMainCafeLocation = binding.cvMainCafeLocation;
        cvMainCafeCountry = binding.cvMainCafeCountry;
        cvMainCafeEmail = binding.cvMainCafeEmail;
        tvMainCafeName = binding.tvMainCafeName;
        tvMainCafeLocation = binding.tvMainCafeLocation;
        tvMainCafeCountry = binding.tvMainCafeCountry;
        tvMainCafeEmail = binding.tvMainCafeEmail;
        tvMainTables = binding.tvOwnerMainTables;
        tvMainNumbers = binding.tvOwnerMainNumbers;
        tvMainCategories = binding.tvOwnerMainCategories;
        tvMainDrinks = binding.tvOwnerMainDrinks;
        cvMainTables = binding.cvOwnerMainTables;
        cvMainEmployees = binding.cvOwnerMainEmployees;
        cvMainCategories = binding.cvOwnerMainCategories;
        cvMainDrinks = binding.cvOwnerMainDrinks;

        cvClickMessage();
        mainViewModelObservers();
        cvCafeClickDialog();

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
        DatabaseReference cafeRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeOwner(cafeId));
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeSnapshot) {
                if(!cafeSnapshot.exists()) {
                    return;
                }
                int cafeTotalDrinks = 0;
                Cafe cafe = cafeSnapshot.getValue(Cafe.class);
                tvMainCafeName.setText(cafe.getCafeName());
                tvMainCafeLocation.setText(cafe.getCafeLocation());
                tvMainCafeCountry.setText(cafe.getCafeCountry());
                tvMainCafeEmail.setText(cafe.getCafeOwnerGmail());
                tvMainTables.setText(String.valueOf(cafe.getCafeTables()));
                tvMainCategories.setText(String.valueOf(cafe.getCafeDrinksCategories().size()));

                for(Map.Entry<String, CafeDrinksCategory> cafeDrinksCategoryEntry : cafe.getCafeDrinksCategories().entrySet()) {
                    CafeDrinksCategory cafeDrinksCategory = cafeDrinksCategoryEntry.getValue();
                    cafeTotalDrinks += cafeDrinksCategory.getCategoryDrinks().size();
                }
                tvMainDrinks.setText(String.valueOf(cafeTotalDrinks));

                collectRegisteredNumbers(cafeId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessage.Title.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime,
                        AppConstValue.errorSender.APP
                );
                appError.sendError(appError);
            }
        });
    }

    private void collectRegisteredNumbers(String cafeId) {
        //firebase
        DatabaseReference registeredNubmersRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeRegisteredNumbers(cafeId));
        registeredNubmersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot registeredNumbersSnapshot) {
                if(!registeredNumbersSnapshot.exists()) {
                    return;
                }
                int numbersCounter = 0;
                for(DataSnapshot registeredNumberSnapshot : registeredNumbersSnapshot.getChildren()) {
                    if(registeredNumberSnapshot.getValue(RegisteredNumber.class).getRole().equals(AppConstValue.registeredNumbersRole.WAITER)) {
                        numbersCounter++;
                    }
                }
                tvMainNumbers.setText(String.valueOf(numbersCounter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessage.Title.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime,
                        AppConstValue.errorSender.APP
                );
                appError.sendError(appError);
            }
        });
    }

    private void cvCafeClickDialog() {
        cvMainCafeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCafeInfo(AppConstValue.cafeInfoClicked.CAFE_NAME);
            }
        });
        cvMainCafeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCafeInfo(AppConstValue.cafeInfoClicked.CAFE_LOCATION);
            }
        });
        cvMainCafeCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCafeInfo(AppConstValue.cafeInfoClicked.CAFE_COUNTRY);
            }
        });
        cvMainCafeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCafeInfo(AppConstValue.cafeInfoClicked.CAFE_EMAIL);
            }
        });
    }

    private void changeCafeInfo(int clickedCafeInfo) {
        final int clickedInfo = clickedCafeInfo;
        View updateCafeInfoView = getLayoutInflater().inflate(R.layout.dialog_cafe_update, null);
        TextInputLayout tilOwnerEmailUpdate = (TextInputLayout) updateCafeInfoView.findViewById(R.id.tilOwnerEmailUpdate);
        TextInputLayout tilCountryStandards = (TextInputLayout) updateCafeInfoView.findViewById(R.id.tilCountryStandards);
        EditText etOwnerEmailUpdate = (EditText) updateCafeInfoView.findViewById(R.id.etOwnerEmailUpdate);
        EditText etCafeUpdate = (EditText) updateCafeInfoView.findViewById(R.id.etCafeUpdate);
        AutoCompleteTextView actvCountryStandards = (AutoCompleteTextView) updateCafeInfoView.findViewById(R.id.actvCountryStandards);
        Button btnCafeUpdateConfirm = (Button) updateCafeInfoView.findViewById(R.id.btnCafeUpdateConfirm);
        ImageButton ibCloseCafeUpdate = (ImageButton) updateCafeInfoView.findViewById(R.id.ibCloseCafeUpdate);

        final AlertDialog updateCafeInfoDialog = new AlertDialog.Builder(getActivity())
                .setView(updateCafeInfoView)
                .create();
        updateCafeInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateCafeInfoDialog.setCanceledOnTouchOutside(false);


        switch(clickedCafeInfo) {
            case 0:
            case 1: {
                updateCafeInfoDialog.setOnShowListener(dialogInterface -> {
                    tilOwnerEmailUpdate.setVisibility(View.GONE);
                    tilCountryStandards.setVisibility(View.GONE);
                    etCafeUpdate.setVisibility(View.VISIBLE);

                    if(clickedInfo == AppConstValue.cafeInfoClicked.CAFE_NAME) {
                        etCafeUpdate.setText(tvMainCafeName.getText().toString());
                        etCafeUpdate.setHint(getResources().getString(R.string.main_cafe_name_hint));
                    }
                    else {
                        etCafeUpdate.setText(tvMainCafeLocation.getText().toString());
                        etCafeUpdate.setHint(getResources().getString(R.string.main_cafe_location_hint));
                    }

                    btnCafeUpdateConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(etCafeUpdate.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                toastMessage.showToast(getResources().getString(R.string.main_cafe_info_update_fail), 0);
                            }
                            else {

                                if(clickedInfo == AppConstValue.cafeInfoClicked.CAFE_NAME) {
                                    DatabaseReference cafeNameRef = firebaseDatabase.getReference(
                                            firebaseRefPaths.getCafeNameOwner(mainViewModel.getOwnerCafeId().getValue()));
                                    cafeNameRef.setValue(etCafeUpdate.getText().toString());
                                }
                                else {
                                    DatabaseReference cafeLocationRef = firebaseDatabase.getReference(
                                            firebaseRefPaths.getCafeLocationOwner(mainViewModel.getOwnerCafeId().getValue()));
                                    cafeLocationRef.setValue(etCafeUpdate.getText().toString());
                                }
                                updateCafeInfoDialog.dismiss();
                                toastMessage.showToast(getResources().getString(R.string.main_cafe_info_update), 0);
                            }
                        }
                    });

                    ibCloseCafeUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateCafeInfoDialog.dismiss();
                        }
                    });
                });
                break;
            }
            case 2: {
                updateCafeInfoDialog.setOnShowListener(dialogInterface -> {
                    etCafeUpdate.setVisibility(View.GONE);
                    tilOwnerEmailUpdate.setVisibility(View.GONE);
                    tilCountryStandards.setVisibility(View.VISIBLE);

                    btnCafeUpdateConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(selectedCodeLocale.equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                toastMessage.showToast(getResources().getString(R.string.main_cafe_country_update_fail), 0);
                            }
                            else {
                                DatabaseReference cafeCountryRef = firebaseDatabase.getReference(
                                        firebaseRefPaths.getCafeCountryOwner(mainViewModel.getOwnerCafeId().getValue()));
                                cafeCountryRef.setValue(selectedCodeLocale);
                                updateCafeInfoDialog.dismiss();
                                toastMessage.showToast(getResources().getString(R.string.main_cafe_country_update_fail), 0);
                            }
                        }
                    });

                    ibCloseCafeUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateCafeInfoDialog.dismiss();
                        }
                    });
                });
                DatabaseReference registeredCountriesRef = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredCountries());
                registeredCountriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot registeredCountriesSnapshot) {
                        if(!registeredCountriesSnapshot.exists()) {
                            return;
                        }
                        ArrayList<RegisteredCountry> registeredCountries = new ArrayList<>();
                        selectedCodeLocale = AppConstValue.variableConstValue.EMPTY_VALUE;

                        for(DataSnapshot registeredCountrieSnapshot : registeredCountriesSnapshot.getChildren()) {
                            if(Objects.equals(registeredCountrieSnapshot.getKey(), tvMainCafeCountry.getText().toString())) {
                                actvCountryStandards.setText(registeredCountrieSnapshot.getValue(RegisteredCountry.class).getNameLocale());
                            }
                            else {
                                registeredCountries.add(registeredCountrieSnapshot.getValue(RegisteredCountry.class));
                            }
                        }
                        CountryItemAdapter countryItemAdapter = new CountryItemAdapter(
                                getActivity(),
                                registeredCountries,
                                R.layout.item_country
                            );
                        actvCountryStandards.setAdapter(countryItemAdapter);

                        actvCountryStandards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Object item = adapterView.getItemAtPosition(i);
                                if(item instanceof RegisteredCountry) {
                                    RegisteredCountry registeredCountryItem = (RegisteredCountry) item;
                                    selectedCodeLocale = registeredCountryItem.getCodeLocale().toString();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                        serverAlertDialog.makeAlertDialog();

                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                mainViewModel.getOwnerCafeId().getValue(),
                                mainViewModel.getOwnerPhoneNumber().getValue(),
                                AppErrorMessage.Title.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                                error.getMessage().toString(),
                                currentDateTime,
                                AppConstValue.errorSender.APP
                        );
                        appError.sendError(appError);
                    }
                });
                break;
            }
            case 3: {
                updateCafeInfoDialog.setOnShowListener(dialogInterface -> {
                    etCafeUpdate.setVisibility(View.GONE);
                    tilCountryStandards.setVisibility(View.GONE);
                    tilOwnerEmailUpdate.setVisibility(View.VISIBLE);
                    etOwnerEmailUpdate.requestFocus();
                    etOwnerEmailUpdate.setText(tvMainCafeEmail.getText().toString());

                    btnCafeUpdateConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(EmailValidator.isEmailValid(etOwnerEmailUpdate.getText().toString())) {
                                DatabaseReference cafeOwnerEmailRef = firebaseDatabase.getReference(
                                        firebaseRefPaths.getCafeOwnerEmail(mainViewModel.getOwnerCafeId().getValue()));
                                cafeOwnerEmailRef.setValue(etOwnerEmailUpdate.getText().toString());
                                updateCafeInfoDialog.dismiss();
                                toastMessage.showToast(getResources().getString(R.string.main_cafe_info_update), 0);
                            }
                            else {
                                toastMessage.showToast(getResources().getString(R.string.main_cafe_email_fail), 0);
                            }
                        }
                    });

                    ibCloseCafeUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateCafeInfoDialog.dismiss();
                        }
                    });
                });
                break;
            }
        }
        updateCafeInfoDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}