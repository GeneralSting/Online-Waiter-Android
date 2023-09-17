package com.example.onlinewaiter.Models;

import java.util.HashMap;

public class Cafe {
    private String cafeLocation, cafeName, cafeOwnerGmail, cafeOwnerLastname, cafeOwnerName, cafeOwnerOib, cafeOwnerPhoneNumber, cafeCountry;
    private Integer cafeTables, cafeTablesStatistic;
    private HashMap<String, CafeDrinksCategory> cafeDrinksCategories;
    private HashMap<String, CafeBill> cafeBills;

    public Cafe() {
        // Default constructor required for calls to DataSnapshot.getValue(Cafe.class)
    }

    public Cafe(String cafeCountry, String cafeLocation, String cafeName, Integer cafeTables, String cafeOwnerGmail, String cafeOwnerLastname, String cafeOwnerName,
                String cafeOwnerOib, String cafeOwnerPhoneNumber, HashMap<String, CafeDrinksCategory> cafeDrinksCategories, Integer cafeTablesStatistic) {
        this.cafeCountry = cafeCountry;
        this.cafeLocation = cafeLocation;
        this.cafeName = cafeName;
        this.cafeTables = cafeTables;
        this.cafeOwnerGmail = cafeOwnerGmail;
        this.cafeOwnerLastname = cafeOwnerLastname;
        this.cafeOwnerName = cafeOwnerName;
        this.cafeOwnerOib = cafeOwnerOib;
        this.cafeOwnerPhoneNumber = cafeOwnerPhoneNumber;
        this.cafeDrinksCategories = cafeDrinksCategories;
        this.cafeTablesStatistic = cafeTablesStatistic;
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

    public String getCafeOwnerLastname() {
        return cafeOwnerLastname;
    }

    public void setCafeOwnerLastname(String cafeOwnerLastname) {
        this.cafeOwnerLastname = cafeOwnerLastname;
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

    public String getCafeCountry() {
        return cafeCountry;
    }

    public void setCafeCountry(String cafeCountry) {
        this.cafeCountry = cafeCountry;
    }

    public Integer getCafeTablesStatistic() {
        return cafeTablesStatistic;
    }

    public void setCafeTablesStatistic(Integer cafeTablesStatistic) {
        this.cafeTablesStatistic = cafeTablesStatistic;
    }
}
