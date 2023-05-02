package com.example.onlinewaiter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.example.onlinewaiter.Adapter.AppPagerAdapter;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.AppInfo;
import com.example.onlinewaiter.Models.ViewPagerItem;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.CustomAlertDialog;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Activity views
    Button btnLogin;
    ViewPager2 vpMainPager;

    //global variables/objects
    ArrayList<ViewPagerItem> viewPagerItems = new ArrayList<>();
    CustomAlertDialog customAlertDialog;
    ToastMessage toastMessage;

    //firebase
    FirebaseRefPaths firebaseRefPaths = new FirebaseRefPaths();
    DatabaseReference appInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This flag is not normally set by application code, but set for you by the system
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        toastMessage = new ToastMessage(this);
        /*
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
        btnApplication.startAnimation(shake);*/
        vpMainPager = findViewById(R.id.vpMainDialogPager);
        insertMainPager();
        btnLogin = findViewById(R.id.btnMainLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askRequiredPermissions();
            }
        });
    }

    public void askRequiredPermissions() {
        if (checkRequiredPermissions()) {
            enterLogin();
        }
    }

    private boolean checkRequiredPermissions() {
        String[] requiredAppPermissions = new String[]{
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.POST_NOTIFICATIONS
        };
        int result;
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredAppPermissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (!missingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]), AppConstValue.permissionConstValue.MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void enterLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //global variables/objects
        intent.putExtra(AppConstValue.bundleConstValue.BUNDLE_PHONE_NUMBER, telephonyManager.getLine1Number().toString());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (grantResults.length) {
            case 1: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enterLogin();
                }
                else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_NUMBERS) ||
                            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        permissionsDialogWarning();
                    } else {
                        permissionDeniedDialog();
                    }
                }
            }
            break;
            case 2: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    enterLogin();
                }
                else {
                    if(shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_NUMBERS) ||
                            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        permissionsDialogWarning();
                    }
                    else {
                        permissionDeniedDialog();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void permissionsDialogWarning() {
        customAlertDialog = new CustomAlertDialog(MainActivity.this,
                getResources().getString(R.string.act_main_dialog_permissions_warning_header),
                getResources().getString(R.string.act_main_dialog_permissions_warning_body),
                getResources().getDrawable(R.drawable.dialog_danger));
        customAlertDialog.makeAlertDialog();
    }

    private void permissionDeniedDialog() {
        customAlertDialog = new CustomAlertDialog(MainActivity.this,
                getResources().getString(R.string.act_main_dialog_permissions_denied_header),
                getResources().getString(R.string.act_main_dialog_permissions_denied_body),
                getResources().getDrawable(R.drawable.dialog_warning));
        customAlertDialog.makeAlertDialog();
    }

    private void insertMainPager() {
        appInfoRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefAppInfo());
        appInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppInfo appInfo = snapshot.getValue(AppInfo.class);
                String[] pagerImages = {appInfo.getAppNewsImage(), appInfo.getAppPurposeImage(), appInfo.getAppUserPurposeImage()};
                String[] pagerHeaders = {appInfo.getAppVersion(), getResources().getString(R.string.act_main_pager_app_purpose_header),
                        getResources().getString(R.string.act_main_pager_app_user_purpsoe_header)};
                if(appInfo.getAppNews().equals(AppConstValue.variableConstValue.EMPTY_VALUE) || appInfo.getAppNews() == null) {
                    appInfo.setAppNews(getResources().getString(R.string.act_main_pager_no_news));
                }
                String[] pagerDecriptions = {appInfo.getAppNews(), appInfo.getAppPurpose(), appInfo.getAppUserPurpose()};
                for (int i = 0; i < pagerImages.length; i++) {
                    ViewPagerItem viewPagerItem;
                    if(i == 0) {
                        viewPagerItem = new ViewPagerItem(pagerImages[i], pagerHeaders[i], pagerDecriptions[i], true);
                    }
                    else {
                        viewPagerItem = new ViewPagerItem(pagerImages[i], pagerHeaders[i], pagerDecriptions[i], false);
                    }
                    viewPagerItems.add(viewPagerItem);
                }
                AppPagerAdapter appPagerAdapter = new AppPagerAdapter(MainActivity.this, viewPagerItems);
                vpMainPager.setAdapter(appPagerAdapter);
                vpMainPager.setClipToPadding(false);
                vpMainPager.setClipChildren(false);
                vpMainPager.setOffscreenPageLimit(2);
                vpMainPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(MainActivity.this);
                serverAlertDialog.makeAlertDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //user did not manaully logged out,
        if(currentUser != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
            String currentDateTime = simpleDateFormat.format(new Date());
            AppError appError = new AppError(
                    AppErrorMessages.Messages.CAFE_NOT_FOUND,
                    currentUser.getPhoneNumber(),
                    AppErrorMessages.Messages.USER_NOT_LOGGED_OUT,
                    currentDateTime
            );
            appError.sendError(appError);

            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
        }
    }
}