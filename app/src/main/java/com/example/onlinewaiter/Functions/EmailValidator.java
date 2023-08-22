package com.example.onlinewaiter.Functions;

import android.text.TextUtils;
import android.util.Patterns;

public class EmailValidator {
    public static boolean isEmailValid(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
