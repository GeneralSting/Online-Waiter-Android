package com.example.onlinewaiter.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.Cafe;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.ViewHolder.RegisteredNumberViewHolder;
import com.example.onlinewaiter.ownerUI.main.MainViewModel;
import com.example.onlinewaiter.ownerUI.registeredNumbers.RegisteredNumbersViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import papaya.in.sendmail.SendMail;

public class MailService {
    private final String mOnlineWaiterMail = "AAppOnlineWaiter@gmail.com";
    private final String mOnlineWaiterPass = "hgbgjpjnswoubyvy";
    private String mReceiverSubject, mReceiverBody, mReceiverMail, mReceiverBodyInfo;
    private final ViewModelStoreOwner mViewModelStoreOwner;
    MainViewModel mainViewModel;


    public MailService(Context context, ViewModelStoreOwner viewModelStoreOwner ,String receiverSubject, String receiverBody, String receiverBodyInfo) {
        this.mViewModelStoreOwner = viewModelStoreOwner;
        mainViewModel = new ViewModelProvider(mViewModelStoreOwner).get(MainViewModel.class);
        SendMail(context, viewModelStoreOwner, receiverSubject, receiverBodyInfo, receiverBody);
    }

    public void SendMail(Context context, ViewModelStoreOwner ViewModelStoreOwner, String receiverSubject, String receiverBodyInfo, String receiverBody) {
        FirebaseRefPaths firebaseRefPaths = new FirebaseRefPaths();
        DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getOwnerRefCafe(mainViewModel.getOwnerCafeId().getValue()));
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeSnapshot) {
                mReceiverMail = Objects.requireNonNull(cafeSnapshot.getValue(Cafe.class)).getCafeOwnerGmail();
                mReceiverSubject = receiverSubject;
                mReceiverBodyInfo = receiverBodyInfo;
                final int randomCode = new Random().nextInt((AppConstValue.emailConstValues.RANDOM_BOUND - AppConstValue.emailConstValues.RANDOM_ORIGIN) +
                        1) + AppConstValue.emailConstValues.RANDOM_ORIGIN;
                RegisteredNumbersViewModel registeredNumbersViewModel = new ViewModelProvider(mViewModelStoreOwner).get(RegisteredNumbersViewModel.class);
                registeredNumbersViewModel.setEmailRandomCode(randomCode);
                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_CRO, Locale.getDefault());
                mReceiverBody = receiverBody + AppConstValue.characterConstValue.CHARACTER_SPACING +
                        String.valueOf(randomCode) + "\n" + receiverBodyInfo + AppConstValue.characterConstValue.CHARACTER_SPACING + simpleDateFormat.format(currentDate);

                SendMail mail = new SendMail(mOnlineWaiterMail, mOnlineWaiterPass,
                        mReceiverMail, mReceiverSubject, mReceiverBody);
                mail.execute();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
                AppError appError;
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(context);
                serverAlertDialog.makeAlertDialog();
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessages.Messages.SENDING_EMAIL_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }
}
