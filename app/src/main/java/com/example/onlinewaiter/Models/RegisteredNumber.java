package com.example.onlinewaiter.Models;

public class RegisteredNumber {
    String number, cafeId, role;
    boolean allowed;

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

    public RegisteredNumber(String cafeId, String role, boolean allowed) {
        this.cafeId = cafeId;
        this.role = role;
        this.allowed = allowed;
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

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
