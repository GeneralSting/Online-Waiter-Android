package com.example.onlinewaiter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.ownerUI.main.MainViewModel;
import com.example.onlinewaiter.ownerUI.registeredNumbers.RegisteredNumbersViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.onlinewaiter.databinding.ActivityOwnerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class OwnerActivity extends AppCompatActivity {
    //global variables/objects
    private FirebaseAuth firebaseAuth;
    ToastMessage toastMessage;
    String ownerNumber, ownerCafeId;
    int updateCafeInfo = 0;
    private static long back_pressed;
    MainViewModel mainViewModel;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
    private AppError appError;

    //firebase
    private DatabaseReference cafeRef;
    private FirebaseRefPaths firebaseRefPaths;
    ChildEventListener cafeChildListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.onlinewaiter.databinding.ActivityOwnerBinding binding = ActivityOwnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toastMessage = new ToastMessage(this);
        firebaseRefPaths = new FirebaseRefPaths(this);
        Bundle bundle = getIntent().getExtras();
        ownerNumber = bundle.getString(AppConstValue.bundleConstValue.BUNDLE_PHONE_NUMBER);
        ownerCafeId = bundle.getString(AppConstValue.bundleConstValue.BUNDLE_CAFE_ID);
        if(Objects.equals(ownerNumber, AppConstValue.variableConstValue.EMPTY_VALUE) ||
                Objects.equals(ownerCafeId, AppConstValue.variableConstValue.EMPTY_VALUE)) {
            logout();
        }

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setOwnerPhoneNumber(ownerNumber);
        mainViewModel.setOwnerCafeId(ownerCafeId);

        BottomNavigationView navView = findViewById(R.id.nav_owner_view);
        navView.getMenu().findItem(R.id.nav_owner_logout).setOnMenuItemClickListener(menuItem -> {
            View ownerLogoutView = getLayoutInflater().inflate(R.layout.dialog_owner_logout, null);
            TextView tvOwnerLogoutNumberChanged = ownerLogoutView.findViewById(R.id.tvOwnerLogoutNumberChanged);
            ImageButton ibCloseOwnerLogout = ownerLogoutView.findViewById(R.id.ibCloseOwnerLogout);
            Button btnOwnerLogoutAccept = ownerLogoutView.findViewById(R.id.btnOwnerLogoutAccept);
            RegisteredNumbersViewModel registeredNumbersViewModel = new ViewModelProvider(this).get(RegisteredNumbersViewModel.class);
            if(registeredNumbersViewModel.getPhoneNumberChanged().getValue()) {
                tvOwnerLogoutNumberChanged.setText(getResources().getString(R.string.logout_owner_number_changed));
            }

            final AlertDialog ownerLogoutDialog = new AlertDialog.Builder(this)
                    .setView(ownerLogoutView)
                    .create();
            ownerLogoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ownerLogoutDialog.setCanceledOnTouchOutside(false);
            ownerLogoutDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    ibCloseOwnerLogout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ownerLogoutDialog.dismiss();
                        }
                    });

                    btnOwnerLogoutAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            logout();
                            ownerLogoutDialog.dismiss();
                        }
                    });
                }
            });
            ownerLogoutDialog.show();

            return true;
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_owner);
        NavigationUI.setupWithNavController(binding.navOwnerView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getOwnerRefCafe(mainViewModel.getOwnerCafeId().getValue()));
        cafeChildListener = cafeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot cafeSnapshot, @Nullable String previousChildName) {
                if(!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                    removeCafeChildListener();
                }
                if(Objects.requireNonNull(cafeSnapshot.getKey()).equals(firebaseRefPaths.getRefCafeCurrentOrdersChild()) ||
                        Objects.requireNonNull(cafeSnapshot.getKey()).equals(firebaseRefPaths.getRefCafeBillsChild())) {

                }
                else {
                    mainViewModel.setUpdateInfo(updateCafeInfo);
                    updateCafeInfo++;
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void removeCafeChildListener() {
        cafeRef.removeEventListener(cafeChildListener);
    }

    private void logout() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(OwnerActivity.this, MainActivity.class));
            finish();
        }
        if(currentUser == null) {
            startActivity(new Intent(OwnerActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(back_pressed + AppConstValue.variableConstValue.EXIT_TIME_DELAY > System.currentTimeMillis()) {
            logout();
        }
        else {
            toastMessage.showToast(getResources().getString(R.string.act_employee_back_button_pressed), 0);
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCafeChildListener();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}