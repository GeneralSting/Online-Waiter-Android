package com.example.onlinewaiter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //Activity views
    Button btnLogin, btnApplication;

    //global variables/objects
    private String phoneNumber = "";

    //premissions codes
    int REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER = 102;

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

        btnApplication = findViewById(R.id.btnMainApplication);

        btnApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                btnApplication.startAnimation(shake);
            }
        });
    }
}