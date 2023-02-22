package com.example.onlinewaiter.Models;

public class CountryNumber {
    String countryName, countryFlag, countryNameLocal, countryNumberCode;

    public CountryNumber() {}

    public CountryNumber(String countryName, String countryFlag, String countryNameLocal, String countryNumberCode) {
        this.countryName = countryName;
        this.countryFlag = countryFlag;
        this.countryNameLocal = countryNameLocal;
        this.countryNumberCode = countryNumberCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public String getCountryNameLocal() {
        return countryNameLocal;
    }

    public String getCountryNumberCode() {
        return countryNumberCode;
    }
}
