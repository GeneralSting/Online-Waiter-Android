package com.example.onlinewaiter.Models;

public class AppError {
    String cafeId, phoneNumber, errorMessage, dateTime;

    public AppError() {}

    public AppError(String cafeId, String phoneNumber, String errorMessage, String dateTime) {
        this.cafeId = cafeId;
        this.phoneNumber = phoneNumber;
        this.errorMessage = errorMessage;
        this.dateTime = dateTime;
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
}
