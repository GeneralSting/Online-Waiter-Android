package com.example.onlinewaiter.Models;

public class RegisteredNumber {
    private String number, role;
    private boolean allowed;
    private int webAppRegistered;

    public RegisteredNumber() {}

    public RegisteredNumber(String role) {
        this.role = role;
    }

    public RegisteredNumber(String role, boolean allowed) {
        this.role = role;
        this.allowed = allowed;
    }

    public RegisteredNumber(String role, boolean allowed, int webAppRegistered) {
        this.role = role;
        this.allowed = allowed;
        this.webAppRegistered = webAppRegistered;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public int getWebAppRegistered() {
        return webAppRegistered;
    }

    public void setWebAppRegistered(int webAppRegistered) {
        this.webAppRegistered = webAppRegistered;
    }
}
