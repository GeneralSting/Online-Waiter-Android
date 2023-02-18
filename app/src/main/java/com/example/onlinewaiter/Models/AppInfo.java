package com.example.onlinewaiter.Models;

public class AppInfo {
    String appNews, appPurpose, appUserPurpose, appVersion, appNewsImage, appPurposeImage, appUserPurposeImage;

    //for firebase
    public AppInfo() {}

    public AppInfo(String appNews, String appPurpose, String appUserPurpose, String appVersion,
                   String appNewsImage, String appPurposeImage, String appUserPurposeImage) {
        this.appNews = appNews;
        this.appPurpose = appPurpose;
        this.appUserPurpose = appUserPurpose;
        this.appVersion = appVersion;
        this.appNewsImage = appNewsImage;
        this.appPurposeImage = appPurposeImage;
        this.appUserPurposeImage = appUserPurposeImage;
    }

    public String getAppNews() {
        return appNews;
    }

    public String getAppPurpose() {
        return appPurpose;
    }

    public String getAppUserPurpose() {
        return appUserPurpose;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppNewsImage() {
        return appNewsImage;
    }

    public String getAppPurposeImage() {
        return appPurposeImage;
    }

    public String getAppUserPurposeImage() {
        return appUserPurposeImage;
    }

    public void setAppNews(String appNews) {
        this.appNews = appNews;
    }
}
