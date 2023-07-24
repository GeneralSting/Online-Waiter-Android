package com.example.onlinewaiter.Other;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {
    private static Toast toast;
    private Context mContext;

    public ToastMessage(Context context) {
        this.mContext = context;
    }

    public void showToast(String text, int duration) {
        if(text != null && mContext != null) {
            if(toast != null) {
                toast.cancel();
            }
            if(duration == 0) {
                toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            }
            else {
                toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
            }
            toast.show();
        }
    }
}
