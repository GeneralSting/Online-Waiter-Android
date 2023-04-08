package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class Cafe {
    String cafeLocation, cafeName, cafeOwnerGmail, getCafeOwnerLastname, cafeOwnerName, cafeOwnerOib, cafeOwnerPhoneNumber;
    Integer cafeTables;
    HashMap<String, CafeDrinksCategory> cafeDrinksCategories;
    HashMap<String, CafeBill> cafeBills;
    HashMap<String, CafeEmployee> cafeEmployees;

    public Cafe() {
        // Default constructor required for calls to DataSnapshot.getValue(Cafe.class)
    }

    public Cafe(String cafeLocation, String cafeName, Integer cafeTables, String cafeOwnerGmail, String getCafeOwnerLastname, String cafeOwnerName,
                String cafeOwnerOib, String cafeOwnerPhoneNumber, HashMap<String, CafeDrinksCategory> cafeDrinksCategories,
                HashMap<String, CafeBill> cafeBills, HashMap<String, CafeEmployee> cafeEmployees) {
        this.cafeLocation = cafeLocation;
        this.cafeName = cafeName;
        this.cafeTables = cafeTables;
        this.cafeOwnerGmail = cafeOwnerGmail;
        this.getCafeOwnerLastname = getCafeOwnerLastname;
        this.cafeOwnerName = cafeOwnerName;
        this.cafeOwnerOib = cafeOwnerOib;
        this.cafeOwnerPhoneNumber = cafeOwnerPhoneNumber;
        this.cafeDrinksCategories = cafeDrinksCategories;
        this.cafeBills = cafeBills;
        this.cafeEmployees = cafeEmployees;
    }

    public String getCafeLocation() {
        return cafeLocation;
    }

    public void setCafeLocation(String cafeLocation) {
        this.cafeLocation = cafeLocation;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public String getCafeOwnerGmail() {
        return cafeOwnerGmail;
    }

    public void setCafeOwnerGmail(String cafeOwnerGmail) {
        this.cafeOwnerGmail = cafeOwnerGmail;
    }

    public String getGetCafeOwnerLastname() {
        return getCafeOwnerLastname;
    }

    public void setGetCafeOwnerLastname(String getCafeOwnerLastname) {
        this.getCafeOwnerLastname = getCafeOwnerLastname;
    }

    public String getCafeOwnerName() {
        return cafeOwnerName;
    }

    public void setCafeOwnerName(String cafeOwnerName) {
        this.cafeOwnerName = cafeOwnerName;
    }

    public String getCafeOwnerOib() {
        return cafeOwnerOib;
    }

    public void setCafeOwnerOib(String cafeOwnerOib) {
        this.cafeOwnerOib = cafeOwnerOib;
    }

    public String getCafeOwnerPhoneNumber() {
        return cafeOwnerPhoneNumber;
    }

    public void setCafeOwnerPhoneNumber(String cafeOwnerPhoneNumber) {
        this.cafeOwnerPhoneNumber = cafeOwnerPhoneNumber;
    }

    public Integer getCafeTables() {
        return cafeTables;
    }

    public void setCafeTables(Integer cafeTables) {
        this.cafeTables = cafeTables;
    }

    public HashMap<String, CafeDrinksCategory> getCafeDrinksCategories() {
        return cafeDrinksCategories;
    }

    public void setCafeDrinksCategories(HashMap<String, CafeDrinksCategory> cafeDrinksCategories) {
        this.cafeDrinksCategories = cafeDrinksCategories;
    }

    public HashMap<String, CafeBill> getCafeBills() {
        return cafeBills;
    }

    public void setCafeBills(HashMap<String, CafeBill> cafeBills) {
        this.cafeBills = cafeBills;
    }
}
