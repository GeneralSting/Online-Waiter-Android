package com.example.onlinewaiter.Other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.onlinewaiter.Interfaces.SmsBroadcastReceiverListener;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.Objects;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public SmsBroadcastReceiverListener smsBroadcastReceiverListener;

    public void initListener(SmsBroadcastReceiverListener smsBroadcastReceiverListener) {
        this.smsBroadcastReceiverListener = smsBroadcastReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), SmsRetriever.SMS_RETRIEVED_ACTION)){
            Bundle extras = intent.getExtras();
            Status smsRetreiverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (smsRetreiverStatus.getStatusCode()){

                case CommonStatusCodes
                        .SUCCESS:
                    Intent messageIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                    smsBroadcastReceiverListener.onSuccess(messageIntent);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    smsBroadcastReceiverListener.onFailure();
                    break;
            }
        }
    }
}
