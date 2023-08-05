package com.example.onlinewaiter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.onlinewaiter.Adapter.OrderDrinksAdapter;
import com.example.onlinewaiter.Adapter.OwnerCafeAdapter;
import com.example.onlinewaiter.Functions.SortHashMap;
import com.example.onlinewaiter.Interfaces.ItemClickListener;
import com.example.onlinewaiter.Interfaces.OwnerCafeClick;
import com.example.onlinewaiter.Interfaces.SmsBroadcastReceiverListener;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.Cafe;
import com.example.onlinewaiter.Models.RegisteredNumber;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements OwnerCafeClick {

    //Activity views
    private EditText etPhoneNumber, etRecivedOtp;
    private ImageView ivNumberQuestion;
    private Button btnSendPhoneNumber, btnVerifyOtp;
    private ProgressBar loginProgressBar;
    AlertDialog dialogOnweCafes;

    //global variables/objects
    private String phoneNumber = AppConstValue.variableConstValue.EMPTY_VALUE;
    private String authNumber, verificationId, numberRole, numberCafeId, numberMemoryWord;
    private String sharedPhoneNumber = AppConstValue.variableConstValue.EMPTY_VALUE;
    private boolean numberFounded, backPressEnabled;
    boolean saveSharedPhoneNumber = false;
    int registeredNumberWeb;
    List<String> ownerCafes = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private ToastMessage toastMessage;
    private SmsBroadcastReceiver smsBroadcastReceiver;

    //firebase
    private FirebaseAuth mAuth;
    private final FirebaseRefPaths firebaseRefPaths = new FirebaseRefPaths();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        numberFounded = false;
        backPressEnabled = true;
        authNumber = numberRole = AppConstValue.variableConstValue.EMPTY_VALUE;
        mAuth = FirebaseAuth.getInstance();
        toastMessage = new ToastMessage(this);

        etPhoneNumber = findViewById(R.id.etLoginEnterNumber);
        etRecivedOtp = findViewById(R.id.etLoginEnterOtp);
        btnSendPhoneNumber = findViewById(R.id.btnLoginSendNumber);
        btnVerifyOtp = findViewById(R.id.btnLoginVerifyOtp);
        loginProgressBar = findViewById(R.id.pbLogin);
        ivNumberQuestion = findViewById(R.id.ivLoginNumberQuestion);

        setPhoneNumber();
        btnSendNumberAction();
        btnVerifyOtpAction();
        activateOtpReceiver();
    }

    private void setPhoneNumber() {
        sharedPreferences = getSharedPreferences(AppConstValue.sharedPreferencesValue.PREFERENCE_PHONE_NUMBER, MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        if (Objects.equals(bundle.getString(AppConstValue.bundleConstValue.LOGIN_PHONE_NUMBER), AppConstValue.variableConstValue.EMPTY_VALUE)) {
            saveSharedPhoneNumber = true;
            if (sharedPreferences.contains(AppConstValue.sharedPreferencesValue.SHARED_PHONE_NUMBER)) {
                sharedPhoneNumber = sharedPreferences.getString(
                        AppConstValue.sharedPreferencesValue.SHARED_PHONE_NUMBER,
                        AppConstValue.sharedPreferencesValue.PREFERENCE_PHONE_NUMBER);
                phoneNumber = sharedPhoneNumber;
                etPhoneNumber.setText(phoneNumber);
                ivNumberQuestion.setImageResource(R.drawable.icon_baseline_question_mark_success_16);
                ivNumberQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(LoginActivity.this,
                                getResources().getString(R.string.act_login_dialog_shared_number_header),
                                getResources().getString(R.string.act_login_dialog_shared_number_body),
                                getResources().getDrawable(R.drawable.modal_no_number));
                        customAlertDialog.makeAlertDialog();
                    }
                });
            }
            else {
                ivNumberQuestion.setImageResource(R.drawable.icon_baseline_question_mark_16);
                ivNumberQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(LoginActivity.this,
                                getResources().getString(R.string.act_login_dialog_no_number_header),
                                getResources().getString(R.string.act_login_dialog_no_number_body),
                                getResources().getDrawable(R.drawable.modal_no_number));
                        customAlertDialog.makeAlertDialog();
                    }
                });
            }
        } else {
            ivNumberQuestion.setImageResource(R.drawable.icon_baseline_download_done_16);
            ivNumberQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toastMessage.showToast(getResources().getString(R.string.act_login_successful_taken_number), 0);
                }
            });
            phoneNumber = bundle.getString(AppConstValue.bundleConstValue.LOGIN_PHONE_NUMBER);
            etPhoneNumber.setText(phoneNumber);
        }
    }

    private void btnSendNumberAction() {
        btnSendPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // regex to check the validity of the mobile phone number, it works for Croatian numbers and other countries
                String phoneNumberValidator = AppConstValue.regex.PHONE_NUMBER_VALIDATOR;
                if (!(etPhoneNumber.getText().toString().matches(phoneNumberValidator))) {
                    toastMessage.showToast(getResources().getString(R.string.act_login_phone_number_incorrect), 0);
                }
                else {
                    btnSendPhoneNumber.setEnabled(false);
                    backPressEnabled = false;
                    authNumber = etPhoneNumber.getText().toString();
                    loginProgressBar.setVisibility(View.VISIBLE);
                    checkNumber();
                }
            }
        });
    }

    private void checkNumber() {
        DatabaseReference registeredNumbers = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRegisteredNumbers());
        //addListenerForSingleValueEvent -> only once will go through database, we do not need continuously listen here
        registeredNumbers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeRegisteredNumbersSnapshot) {
                int ownerCounter = 0;
                registeredCafesLoop:
                for (DataSnapshot cafeRegisteredNumberSnapshot : cafeRegisteredNumbersSnapshot.getChildren()) {
                    for(DataSnapshot registeredNumberSnapshot : cafeRegisteredNumberSnapshot.getChildren()) {
                        if (authNumber.equals(registeredNumberSnapshot.getKey())) {
                            numberFounded = true;
                            RegisteredNumber registeredNumber = registeredNumberSnapshot.getValue(RegisteredNumber.class);
                            if(registeredNumber.getRole().equals(AppConstValue.registeredNumbersRole.OWNER)) {
                                if(ownerCounter == 0) {
                                    numberRole = registeredNumber.getRole();
                                    phoneNumber = authNumber;
                                }
                                ownerCounter++;
                                ownerCafes.add(cafeRegisteredNumberSnapshot.getKey());
                                break;
                            }
                            else if(registeredNumber.getRole().equals(AppConstValue.registeredNumbersRole.WAITER) && !registeredNumber.isAllowed()) {
                                toastMessage.showToast(getResources().getString(R.string.act_login_number_not_allowed), 0);
                                loginProgressBar.setVisibility(View.INVISIBLE);
                                btnSendPhoneNumber.setEnabled(true);
                                break registeredCafesLoop;
                            }
                            else {
                                numberRole = registeredNumber.getRole();
                                numberCafeId = cafeRegisteredNumberSnapshot.getKey();
                                registeredNumberWeb = registeredNumber.getWebAppRegistered();
                                numberMemoryWord = registeredNumber.getMemoryWord();
                                phoneNumber = authNumber;
                                sendverificationcode(authNumber);
                                break registeredCafesLoop;
                            }
                        }
                    }
                }
                if(numberRole.equals(AppConstValue.registeredNumbersRole.OWNER)) {
                    sendverificationcode(authNumber);
                }
                else if (!numberFounded) {
                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(LoginActivity.this,
                            getResources().getString(R.string.act_login_dialog_no_user_header),
                            getResources().getString(R.string.act_login_dialog_no_user_body),
                            getResources().getDrawable(R.drawable.modal_no_number));
                    customAlertDialog.makeAlertDialog();
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    btnSendPhoneNumber.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(LoginActivity.this);
                serverAlertDialog.makeAlertDialog();

                AppError appError;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        AppErrorMessages.Message.CAFE_NOT_FOUND,
                        phoneNumber,
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void btnVerifyOtpAction() {
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
    }

    private void activateOtpReceiver() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.initListener(new SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, AppConstValue.permissionConstValue.REQ_READING_LOGIN_OTP);
            }

            @Override
            public void onFailure() {

            }
        });

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    private void getOtpFromMessage(String message) {
        Pattern otpPattern = Pattern.compile(AppConstValue.regex.LOGIN_OTP_VALIDATOR);
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {
            btnVerifyOtp.setEnabled(false);
            etRecivedOtp.setText(matcher.group(0));
            verifycode(etRecivedOtp.getText().toString());
        }
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            btnSendPhoneNumber.setEnabled(true);
            backPressEnabled = true;
            super.onCodeSent(s, token);
            verificationId = s;
            toastMessage.showToast(getResources().getString(R.string.act_login_otp_sent), 0);
            btnVerifyOtp.setEnabled(true);
            loginProgressBar.setVisibility(View.INVISIBLE);
        }
    };

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

    private void verifycode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, Code);
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(numberFounded && saveSharedPhoneNumber && !authNumber.equals(sharedPhoneNumber)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AppConstValue.sharedPreferencesValue.SHARED_PHONE_NUMBER, phoneNumber);
                    editor.apply();
                }
                if (numberRole.equals(AppConstValue.registeredNumbersRole.WAITER) && numberFounded) {
                    Intent intent = new Intent(LoginActivity.this, EmployeeActivity.class);
                    //flag -> If set, this activity will become the start of a new task on this history stack.
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AppConstValue.bundleConstValue.LOGIN_CAFE_ID, numberCafeId);
                    intent.putExtra(AppConstValue.bundleConstValue.LOGIN_PHONE_NUMBER, phoneNumber);
                    intent.putExtra(AppConstValue.bundleConstValue.REGISTERED_NUMBER_WEB, registeredNumberWeb);
                    intent.putExtra(AppConstValue.bundleConstValue.NUMBER_MEMORY_WORD, numberMemoryWord);
                    //Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                    //afinitet, srodstvo
                    finishAffinity();
                    startActivity(intent);
                }
                else if(numberRole.equals(AppConstValue.registeredNumbersRole.OWNER) && numberFounded) {
                    checkOwnershipNumber();
                }
            }
            else {
                toastMessage.showToast(getResources().getString(R.string.act_login_wrong_otp), 0);
            }
        });
    }

    private void checkOwnershipNumber() {
        if(ownerCafes.size() > 1) {
           collectOwnerCafes();
        }
        else {
            selectedOwnerCafe(ownerCafes.get(0), false);
        }
    }

    private void collectOwnerCafes() {
        HashMap<String, Cafe> collectedOwnerCafes = new HashMap<>();
        DatabaseReference cafesRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getCafes());
        cafesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafesSnapshot) {
                for(DataSnapshot cafeSnapshot : cafesSnapshot.getChildren()) {
                    for(String ownerCafe : ownerCafes) {
                        if(ownerCafe.equals(cafeSnapshot.getKey())) {
                            collectedOwnerCafes.put(ownerCafe, cafeSnapshot.getValue(Cafe.class));
                            break;
                        }
                    }
                }

                View ownerCafeView = getLayoutInflater().inflate(R.layout.dialog_owner_cafes, null);
                ImageButton ibCloseOwnerCafes = ownerCafeView.findViewById(R.id.ibCloseOwnerCafes);
                RecyclerView rvOwnerCafes = ownerCafeView.findViewById(R.id.rvOwnerCafes);
                rvOwnerCafes.setLayoutManager(new LinearLayoutManager(LoginActivity.this));

                OwnerCafeAdapter ownerCafeAdapter = new OwnerCafeAdapter(collectedOwnerCafes,
                        LoginActivity.this, LoginActivity.this);
                rvOwnerCafes.setAdapter(ownerCafeAdapter);
                ownerCafeAdapter.notifyDataSetChanged();

                dialogOnweCafes = new AlertDialog.Builder(LoginActivity.this)
                        .setView(ownerCafeView)
                        .create();
                dialogOnweCafes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogOnweCafes.setCanceledOnTouchOutside(false);

                dialogOnweCafes.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        ibCloseOwnerCafes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogOnweCafes.dismiss();
                            }
                        });
                    }
                });
                dialogOnweCafes.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(LoginActivity.this);
                serverAlertDialog.makeAlertDialog();

                AppError appError;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        AppErrorMessages.Message.CAFE_NOT_FOUND,
                        phoneNumber,
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    @Override
    public void selectedOwnerCafe(String cafeId, boolean closeDialog) {
        if(closeDialog) {
            dialogOnweCafes.dismiss();
        }
        Intent intent = new Intent(LoginActivity.this, OwnerActivity.class);
        //flag -> If set, this activity will become the start of a new task on this history stack.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AppConstValue.bundleConstValue.LOGIN_CAFE_ID, cafeId);
        intent.putExtra(AppConstValue.bundleConstValue.LOGIN_PHONE_NUMBER, phoneNumber);
        //Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
        //afinitet, srodstvo
        finishAffinity();
        startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstValue.permissionConstValue.REQ_READING_LOGIN_OTP) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if(backPressEnabled) {
            super.onBackPressed();
        }
    }
}