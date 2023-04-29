package com.example.onlinewaiter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.onlinewaiter.Interfaces.SmsBroadcastReceiverListener;
import com.example.onlinewaiter.Models.RegisteredNumber;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.CustomAlertDialog;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.SmsBroadcastReceiver;
import com.example.onlinewaiter.Other.ToastMessage;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    //Activity views
    EditText etPhoneNumber, etRecivedOtp;
    ImageView ivNumberQuestion;
    Button btnSendPhoneNumber, btnVerifyOtp;
    ProgressBar loginProgressBar;

    //global variables/objects
    String phoneNumber = "";
    String authNumber, verificationId, numberRole, numberCafeId;
    Boolean numberFounded, showProgressBar, backPressEnabled;
    ToastMessage toastMessage;
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;

    //firebase
    FirebaseAuth mAuth;
    FirebaseRefPaths firebaseRefPaths = new FirebaseRefPaths();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toastMessage = new ToastMessage(this);
        showProgressBar = backPressEnabled = true;
        authNumber = numberRole = "";
        numberFounded = false;
        mAuth = FirebaseAuth.getInstance();

        etPhoneNumber = findViewById(R.id.etLoginEnterNumber);
        etRecivedOtp = findViewById(R.id.etLoginEnterOtp);
        btnSendPhoneNumber = findViewById(R.id.btnLoginSendNumber);
        btnVerifyOtp = findViewById(R.id.btnLoginVerifyOtp);
        loginProgressBar = findViewById(R.id.pbLogin);
        ivNumberQuestion = findViewById(R.id.ivLoginNumberQuestion);

        Bundle bundle = getIntent().getExtras();
        if (Objects.equals(bundle.getString(AppConstValue.constValue.BUNDLE_PHONE_NUMBER), AppConstValue.constValue.EMPTY_VALUE)) {
            ivNumberQuestion.setImageResource(R.drawable.icon_baseline_question_mark_16);
            ivNumberQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(LoginActivity.this,
                            getResources().getString(R.string.act_login_dialog_no_number_header),
                            getResources().getString(R.string.act_login_dialog_no_number_body),
                            getResources().getDrawable(R.drawable.dialog_no_number));
                    customAlertDialog.makeAlertDialog();
                }
            });
        } else {
            ivNumberQuestion.setImageResource(R.drawable.icon_baseline_download_done_16);
            ivNumberQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toastMessage.showToast(getResources().getString(R.string.act_login_successful_taken_number), 0);
                }
            });
            phoneNumber = bundle.getString(AppConstValue.constValue.BUNDLE_PHONE_NUMBER);
            etPhoneNumber.setText(phoneNumber);
        }

        btnSendPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSendPhoneNumber.setEnabled(false);
                backPressEnabled = false;
                // regex to check the validity of the mobile phone number, it works for Croatian numbers and other countries
                String phoneNumberValidator = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$";
                if (!(etPhoneNumber.getText().toString().matches(phoneNumberValidator))) {
                    toastMessage.showToast(getResources().getString(R.string.act_login_phone_number_incorrect), 0);
                }
                else {
                    authNumber = etPhoneNumber.getText().toString();
                    if (showProgressBar) {
                        loginProgressBar.setVisibility(View.VISIBLE);
                        showProgressBar = false;
                    }
                    checkNumber();
                }
            }
        });
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etRecivedOtp.getText().toString())) {
                    toastMessage.showToast(getResources().getString(R.string.act_login_empty_otp), 0);
                } else {
                    verifycode(etRecivedOtp.getText().toString());
                }
            }
        });
        otpReceiver();
    }

    private void otpReceiver() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {
            btnVerifyOtp.setEnabled(false);
            loginProgressBar.setEnabled(true);
            etRecivedOtp.setText(matcher.group(0));
            verifycode(etRecivedOtp.getText().toString());
        }
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.initListener(new SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, REQ_USER_CONSENT);
            }

            @Override
            public void onFailure() {

            }
        });

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //additional verification measure, currentUser should not be true, user can not go to loginActivity
        //if he is not logout from firebase first
        if (currentUser != null) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
        }
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    private void checkNumber() {
        DatabaseReference registeredNumbers = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefRegisteredNumbers());
        //addListenerForSingleValueEvent -> only once will go through database, we do not need continuously listen here
        registeredNumbers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot numberSnapshot : snapshot.getChildren()) {
                    if (authNumber.equals(numberSnapshot.getKey())) {
                        numberFounded = true;
                        RegisteredNumber registeredNumber = numberSnapshot.getValue(RegisteredNumber.class);
                        numberRole = registeredNumber.getRole();
                        numberCafeId = registeredNumber.getCafeId();
                        phoneNumber = authNumber;
                        sendverificationcode(authNumber);
                    }
                }
                if (!numberFounded) {
                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(LoginActivity.this,
                            getResources().getString(R.string.act_login_dialog_no_user_header),
                            getResources().getString(R.string.act_login_dialog_no_user_body),
                            getResources().getDrawable(R.drawable.dialog_no_number));
                    customAlertDialog.makeAlertDialog();
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    showProgressBar = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(LoginActivity.this);
                serverAlertDialog.makeAlertDialog();
            }
        });
        btnSendPhoneNumber.setEnabled(true);
    }

    private void sendverificationcode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)  // Phone number to verify
                        .setTimeout(0L, TimeUnit.SECONDS) // Timeout and unit, 0L because of sms receiver
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        // never been called
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if (code != null) {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            backPressEnabled = true;
            toastMessage.showToast(getResources().getString(R.string.act_login_verification_failed), 0);
            btnSendPhoneNumber.setEnabled(true);
            loginProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            backPressEnabled = false;
            super.onCodeSent(s, token);
            verificationId = s;
            toastMessage.showToast(getResources().getString(R.string.act_login_otp_sent), 0);
            btnVerifyOtp.setEnabled(true);
            loginProgressBar.setVisibility(View.INVISIBLE);
        }
    };

    private void verifycode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, Code);
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential credential) {
        showProgressBar = true;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (numberRole.equals(firebaseRefPaths.getRefRegisteredNumberWaiter()) && numberFounded) {
                            Intent intent = new Intent(LoginActivity.this, EmployeeActivity.class);
                            //flag -> If set, this activity will become the start of a new task on this history stack.
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(AppConstValue.constValue.BUNDLE_CAFE_ID, numberCafeId);
                            intent.putExtra(AppConstValue.constValue.BUNDLE_PHONE_NUMBER, phoneNumber);
                            //Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                            //afinitet, srodstvo
                            finishAffinity();
                            startActivity(intent);
                        }
                        else if(numberRole.equals(firebaseRefPaths.getRefRegisteredNumberOwner()) && numberFounded) {
                            Intent intent = new Intent(LoginActivity.this, OwnerActivity.class);
                            //flag -> If set, this activity will become the start of a new task on this history stack.
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(AppConstValue.constValue.BUNDLE_CAFE_ID, numberCafeId);
                            intent.putExtra(AppConstValue.constValue.BUNDLE_PHONE_NUMBER, phoneNumber);
                            //Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                            //afinitet, srodstvo
                            finishAffinity();
                            startActivity(intent);
                        }
                    }
                    else {
                        toastMessage.showToast(getResources().getString(R.string.act_login_wrong_otp), 0);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(backPressEnabled) {
            super.onBackPressed();
        }
    }
}