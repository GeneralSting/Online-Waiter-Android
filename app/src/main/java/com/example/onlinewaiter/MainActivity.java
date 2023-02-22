package com.example.onlinewaiter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinewaiter.Adapter.AppPagerAdapter;
import com.example.onlinewaiter.Models.AppInfo;
import com.example.onlinewaiter.Models.ViewPagerItem;
import com.example.onlinewaiter.Other.CustomAlertDialog;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //Activity views
    Button btnLogin;
    ViewPager2 vpMainPager;
    ArrayList<ViewPagerItem> viewPagerItems = new ArrayList<>();

    //global variables/objects
    private String phoneNumber = "";
    CustomAlertDialog customAlertDialog;
    private static MainActivity instance;

    //premissions codes
    int REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER = 102;

    //firebase
    String appInfo = "appInfo";
    DatabaseReference appInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, android.R.anim.slide_in_left);
        setContentView(R.layout.activity_main);

        //This flag is not normally set by application code, but set for you by the system
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        /*
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
        btnApplication.startAnimation(shake);*/
        instance = this;
        vpMainPager = findViewById(R.id.vpMainDialogPager);
        insertMainPager();
        btnLogin = findViewById(R.id.btnMainLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proba();
            }
        });
    }

    public void proba() {
        String[] askPhoneNumberPermission = {Manifest.permission.READ_PHONE_NUMBERS};
        requestPermissions(askPhoneNumberPermission, REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, LoginActivity.class);
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //mandatory to have another permission check
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                phoneNumber = telephonyManager.getLine1Number().toString();
                intent.putExtra("phoneNumber", telephonyManager.getLine1Number().toString());
                startActivity(intent);
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_NUMBERS)) //permission deined for the first time -> opening warning modal
                getWarningDialog();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    //a modal that will be displayed if permission is not accepted the first time
    private void getWarningDialog() {
        customAlertDialog = new CustomAlertDialog(MainActivity.this,
                getResources().getString(R.string.act_main_dialog_alert_phone_number_header),
                getResources().getString(R.string.act_main_dialog_alert_phone_number_body),
                getResources().getDrawable(R.drawable.dialog_warning),
                0);
        customAlertDialog.makeAlertDialog();
    }

    private void insertMainPager() {
        appInfoRef = FirebaseDatabase.getInstance().getReference(appInfo);
        appInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppInfo appInfo = snapshot.getValue(AppInfo.class);
                String[] pagerImages = {appInfo.getAppNewsImage(), appInfo.getAppPurposeImage(), appInfo.getAppUserPurposeImage()};
                String[] pagerHeaders = {appInfo.getAppVersion(), getResources().getString(R.string.act_main_pager_app_purpose_header),
                        getResources().getString(R.string.act_main_pager_app_user_purpsoe_header)};
                if(appInfo.getAppNews().equals("") || appInfo.getAppNews() == null) {
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
        /*
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //user is already login, open LoginActivity
        if(currentUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("phoneNumber", phoneNumber.toString());
            finishAffinity();
            startActivity(intent);
        }
        */
    }
}