package com.example.onlinewaiter.Services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.Cafe;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.ownerUI.GlobalViewModel.OwnerViewModel;
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

import papaya.in.sendmail.SendMail;

public class MailService {
    private final String onlineWaiterMail = "AAppOnlineWaiter@gmail.com";
    private final String onlineWaiterPass = "hgbgjpjnswoubyvy";
    private final Context context;
    private final ViewModelStoreOwner viewModelStoreOwner;
    private final MainViewModel mainViewModel;


    public MailService(Context context, ViewModelStoreOwner viewModelStoreOwner) {
        this.context = context;
        this.viewModelStoreOwner = viewModelStoreOwner;
        mainViewModel = new ViewModelProvider(viewModelStoreOwner).get(MainViewModel.class);
    }

    public void appSendMail() {
        FirebaseRefPaths firebaseRefPaths = new FirebaseRefPaths();
        DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getCafeOwner(mainViewModel.getOwnerCafeId().getValue()));
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeSnapshot) {
                String receiverMail = Objects.requireNonNull(cafeSnapshot.getValue(Cafe.class)).getCafeOwnerGmail();
                final int randomCode = new Random().nextInt((AppConstValue.emailConstValue.RANDOM_BOUND - AppConstValue.emailConstValue.RANDOM_ORIGIN) +
                        1) + AppConstValue.emailConstValue.RANDOM_ORIGIN;
                RegisteredNumbersViewModel registeredNumbersViewModel = new ViewModelProvider(viewModelStoreOwner).get(RegisteredNumbersViewModel.class);
                registeredNumbersViewModel.setEmailRandomCode(randomCode);
                OwnerViewModel ownerViewModel = new ViewModelProvider(viewModelStoreOwner).get(OwnerViewModel.class);
                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ownerViewModel.getCafeCountryStandards().getValue().getDateTimeFormat(), Locale.getDefault());
                String receiverBody = ownerViewModel.getCafeCountryStandards().getValue().getMailBody() + AppConstValue.characterConstValue.CHARACTER_SPACING +
                        String.valueOf(randomCode) + "\n" + ownerViewModel.getCafeCountryStandards().getValue().getMailBodyInfo() +
                        AppConstValue.characterConstValue.CHARACTER_SPACING + simpleDateFormat.format(currentDate);

                SendMail mail = new SendMail(onlineWaiterMail, onlineWaiterPass,
                        receiverMail, ownerViewModel.getCafeCountryStandards().getValue().getMailSubject(), receiverBody);
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
                        AppErrorMessages.Message.SENDING_EMAIL_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }
}
