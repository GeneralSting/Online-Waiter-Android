package com.example.onlinewaiter.Models;

public class RegisteredNumber {
    String number, cafeId, role;

    public RegisteredNumber() {}

    public RegisteredNumber(String cafeId, String role) {
        this.cafeId = cafeId;
        this.role = role;
    }

    public RegisteredNumber(String number, String cafeId, String role) {
        this.number = number;
        this.cafeId = cafeId;
        this.role = role;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
