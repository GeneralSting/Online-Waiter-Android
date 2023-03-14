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
import com.example.onlinewaiter.Other.CustomAlertDialog;
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
    String authNumber, verificationId;
    Boolean employeeFounded, bossFounded, showProgressBar;
    ToastMessage toastMessage;
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;


    //firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toastMessage = new ToastMessage(this);
        showProgressBar = true;
        authNumber = "";
        employeeFounded = bossFounded = false;
        mAuth = FirebaseAuth.getInstance();

        etPhoneNumber = findViewById(R.id.etLoginEnterNumber);
        etRecivedOtp = findViewById(R.id.etLoginEnterOtp);
        btnSendPhoneNumber = findViewById(R.id.btnLoginSendNumber);
        btnVerifyOtp = findViewById(R.id.btnLoginVerifyOtp);
        loginProgressBar = findViewById(R.id.pbLogin);
        ivNumberQuestion = findViewById(R.id.ivLoginNumberQuestion);

        Bundle bundle = getIntent().getExtras();
        if (Objects.equals(bundle.getString("phoneNumber"), "")) {
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
            phoneNumber = bundle.getString("phoneNumber");
            etPhoneNumber.setText(phoneNumber);
        }

        btnSendPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // regex to check the validity of the mobile phone number, it works for Croatian numbers and other countries
                String phoneNumberValidator = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$";
                if (!(etPhoneNumber.getText().toString().matches(phoneNumberValidator))) {
                    toastMessage.showToast(getResources().getString(R.string.act_login_phone_number_incorrect), 0);
                } else {
                    authNumber = etPhoneNumber.getText().toString();
                    if (showProgressBar) {
                        loginProgressBar.setVisibility(View.VISIBLE);
                        showProgressBar = false;
                    }
                    btnSendPhoneNumber.setEnabled(false);
                    checkEmployee();
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
                Log.d("PROBA123", "onFailure: ");

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
            //boss is already login, open BossActivity
            if(bossFounded) {
                /*Intent intent = new Intent(this, BossActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                finishAffinity();
                startActivity(intent);*/
            }
            else {
                //user is already login, open HomeActivity
                Intent intent = new Intent(this, EmployeeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                finishAffinity();
                startActivity(intent);
            }
        }
        registerBroadcastReceiver();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    private void checkEmployee() {
        DatabaseReference cafesEmployeesRef = FirebaseDatabase.getInstance().getReference("cafesEmployees");
        //addListenerForSingleValueEvent -> only once will go through database, we do not need continuously listen here
        cafesEmployeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    if (authNumber.equals(employeeSnapshot.getKey())) {
                        employeeFounded = true;
                        sendverificationcode(authNumber);

                    }
                }
                if (!employeeFounded) {
                    checkBoss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(LoginActivity.this);
                serverAlertDialog.makeAlertDialog();
            }
        });
    }

    public void checkBoss() {
        DatabaseReference cafesRef = FirebaseDatabase.getInstance().getReference("cafes");
        //addListenerForSingleValueEvent -> only once will go through database, we do not need continuously listen here
        cafesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot cafeSnapshot : snapshot.getChildren()) {
                    //Cafe cafe = cafeSnapshot.getValue(Cafe.class);
                    //if (authNumber.equals(cafe.getCafeOwnerPhoneNumber())) {
                    //bossFounded = true;
                        /*Intent intent = new Intent(LoginActivity.this, BossActivity.class);
                        //flag -> If set, this activity will become the start of a new task on this history stack.
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                        //Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                        //afinitet, srodstvo
                        finishAffinity();
                        startActivity(intent);*/
                    //}
                }
                if (!employeeFounded && !bossFounded) {
                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(LoginActivity.this,
                            getResources().getString(R.string.act_login_dialog_no_user_header),
                            getResources().getString(R.string.act_login_dialog_no_user_body),
                            getResources().getDrawable(R.drawable.dialog_no_number));
                    customAlertDialog.makeAlertDialog();
                    btnSendPhoneNumber.setEnabled(true);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    showProgressBar = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.database_on_cancel_dialog_header), 0);
            }
        });
    }

    private void sendverificationcode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)  // Phone number to verify
                        .setTimeout(0L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if (code != null) {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            //this might not be enough good code to work alright verification fails
            toastMessage.showToast(getResources().getString(R.string.act_login_verification_failed), 0);
            btnSendPhoneNumber.setEnabled(true);
            loginProgressBar.setVisibility(View.INVISIBLE);
            Log.d("PROBA123", e.toString());
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
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
                        if (employeeFounded) {
                            Intent intent = new Intent(LoginActivity.this, EmployeeActivity.class);
                            //flag -> If set, this activity will become the start of a new task on this history stack.
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                            //Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                            //afinitet, srodstvo
                            finishAffinity();
                            startActivity(intent);
                        } else {
                            //intent boss activity
                        }
                    } else {
                        toastMessage.showToast(getResources().getString(R.string.act_login_wrong_otp), 0);
                    }
                });
    }
}