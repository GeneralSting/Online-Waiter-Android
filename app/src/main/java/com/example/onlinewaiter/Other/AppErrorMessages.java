package com.example.onlinewaiter.Other;

public class AppErrorMessages {
    public static class Messages {
        public static final String USER_NOT_LOGGED_OUT = "On start of application user was logged out, service did not do its job";
        public static final String USER_NOT_FOUND = "";
        public static final String CAFE_NOT_FOUND = "";
        public static final String DOWNLOAD_IMAGE_URI_FAILED = "Failed to download image uri from firebase storage, unable to check weather image needs to be deleted from storage";
        public static final String IMAGE_UPLOAD_TASK_FAILED = "Failed task for updating drink image";
        public static final String RETRIEVING_FIREBASE_DATA_FAILED = "Failed to get data for firebase recycler adapter";
        public static final String RETRIEVING_FIREBASE_DATA_FAILED_OWNER = "Failed to get data for firebase recycler adapter on owner's side";
        public static final String SENDING_EMAIL_FAILED_OWNER = "Failed to send email to owner's e-mail due to changing phone number";
    }
}

