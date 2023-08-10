package com.example.onlinewaiter.Models;

import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppError {
    private String cafeId, phoneNumber, errorTitle, errorMessage, dateTime;
    private int errorSender;

    public AppError() {}

    public AppError(String cafeId, String phoneNumber, String errorTitle, String errorMessage, String dateTime, int errorSender) {
        this.cafeId = cafeId;
        this.phoneNumber = phoneNumber;
        this.errorTitle = errorTitle;
        this.errorMessage = errorMessage;
        this.dateTime = dateTime;
        this.errorSender = errorSender;
    }

    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public int getErrorSender() {
        return errorSender;
    }

    public void setErrorSender(int errorSender) {
        this.errorSender = errorSender;
    }

    public void sendError(AppError appError) {
        FirebaseRefPaths firebaseRefPaths = new FirebaseRefPaths();
        DatabaseReference appErrorRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getAppErrors());
        String dbKey = appErrorRef.push().getKey();
        appErrorRef.child(dbKey).setValue(appError);
    }
}
