package com.example.onlinewaiter.Other;

public class AppErrorMessage {
    public static class Title {
        public static final String LOGIN_SMS_BROADCAST_RECEIVER_FAILED = "Login SMS Broadcast Receiver";
        public static final String USER_NOT_LOGGED_OUT = "User not logged out";
        public static final String DOWNLOAD_IMAGE_URI_FAILED = "Failed to download image uri";
        public static final String IMAGE_UPLOAD_TASK_FAILED = "Failed task for updating drink image";
        public static final String RETRIEVING_FIREBASE_DATA_FAILED = "Failed to get data for firebase recycler adapter";
        public static final String RETRIEVING_FIREBASE_DATA_FAILED_OWNER = "Owner's side, Failed to get data for firebase recycler adapter";
        public static final String SENDING_EMAIL_FAILED_OWNER = "Failed to send email to owner's e-mail due to changing phone number";
        public static final String IMAGE_CROPPER_RESULT_ERROR = "Image Cropper Activity error";
        public static final String USER_BUG_REPORT = "User bug report";
    }

    public static class Other {
        public static final String CAFE_NOT_FOUND = AppConstValue.variableConstValue.EMPTY_VALUE;
    }
    
    public static class Message {
        public static final String LOGIN_SMS_BROADCAST_RECEIVER_FAILED = "SMS Broadcast Receiver listener initialization failed";
        public static final String USER_NOT_LOGGED_OUT = "On start of application user was not logged out, on app killed service not executed";
        public static final String FIREBASE_PATH_REGISTERED_COUNTRY = "Path for registered country code not found, country standards are not collected";
        public static final String FIREBASE_PATH_REGISTERED_COUNTRY_OWNER = "Owner's side, path for registered country code not found, country standards are not collected";
        public static final String DRINK_QUANTITY_UNDER_ZERO = "Employee finished order, new drink quantity is negative number";

    }
}

