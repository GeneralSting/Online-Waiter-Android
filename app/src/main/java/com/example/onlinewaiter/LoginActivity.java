package com.example.onlinewaiter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.onlinewaiter.Adapter.CountriesNumbersAdapter;
import com.example.onlinewaiter.Models.CountryNumber;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    //Activity views
    EditText etPhoneNumber, etRecivedOtp;
    ImageView ivNumberQuestion;
    Button btnSendPhoneNumber, btnVerifyOtp;
    ProgressBar loginProgressBar;

    //global variables/objects
    String phoneNumber = "";
    String authNumber, verificationID;
    Boolean showProgressBar, employeeFounded, bossFounded;
    ToastMessage toastMessage;

    //firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, android.R.anim.slide_out_right);
        setContentView(R.layout.activity_login);

        ivNumberQuestion = findViewById(R.id.ivLoginNumberQuestion);

        Bundle bundle = getIntent().getExtras();
        if (Objects.equals(bundle.getString("phoneNumber"), "")) {
            ivNumberQuestion.setImageResource(R.drawable.icon_baseline_question_mark_16);
            ivNumberQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        else {
            ivNumberQuestion.setImageResource(R.drawable.icon_baseline_download_done_16);
            ivNumberQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toastMessage.showToast(getResources().getString(R.string.act_login_successful_taken_number), 0);
                }
            });
            phoneNumber = bundle.getString("phoneNumber");
        }
    }


}