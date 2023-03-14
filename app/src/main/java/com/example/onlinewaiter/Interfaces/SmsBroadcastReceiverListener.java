package com.example.onlinewaiter.Interfaces;

import android.content.Intent;

public interface SmsBroadcastReceiverListener {
    void onSuccess(Intent intent);
    void onFailure();
}
