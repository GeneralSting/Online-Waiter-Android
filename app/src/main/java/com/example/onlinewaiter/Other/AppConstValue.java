package com.example.onlinewaiter.Other;

public class AppConstValue {
    public static class bundleConstValue {
        public static final String BUNDLE_CAFE_ID = "cafeId";
        public static final String BUNDLE_PHONE_NUMBER = "phoneNumber";
    }

    public static class characterConstValue {
        public static final String CHARACTER_SPACING = " ";
        public static final String IMAGE_NAME_UNDERLINE = "_";
        public static final String PERCENTAGE = "%";
    }

    public static class decimalFormatConstValue {
        public static final String PRICE_DECIMAL_FORMAT_WITH_ZERO = "0.00";
        public static final String PRICE_DECIMAL_FORMAT_NO_ZERO = "#.00";
        public static final String PRICE_DECIMAL_COMMA_NO_ZERO = ",00";
    }

    public static class notificationConstValue {
        public static final String NOTIFICATION_CHANNEL_ID = "ORDER_NOTIFICATION_CHANNEL";
        public static final CharSequence NOTIFICATION_CHANNEL_NAME = "notificationChannelForOrders";
        public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Waiters recieve notification when their order is ready to serve";

    }

    public static class variableConstValue {
        public static final String EMPTY_VALUE = "";
        public static final String DOT = ".";
        public static final String COMMA = ",";
        public static final char CHAR_DOT = '.';
        public static final char CHAR_COMMA = ',';
        public static final String ZERO_VALUE = "0";
        public static final char CHAR_ZERO_VALUE = '0';
        public static final int EXIT_TIME_DELAY = 2000;
        public static final int STATISTICS_DEFAULT_QUERY_SIZE = 100;

    }

    public static class permissionConstValue {
        public static final String CAMERA_EXTRAS_DATA = "data";
        public static final int REQ_USER_CONSENT = 200;
        public static final int MULTIPLE_PERMISSIONS = 124;
        public static final int CAMERA_PERMISSION_CODE = 101;
        public static final int CAMERA_REQUEST_CODE = 102;
        public static final int GALLERY_REQUEST_CODE = 105;
    }

    public static class dateConstValue {
        public static final String DATE_TIME_FORMAT_NORMAL = "yyyy_MM_dd_hh_mm_ss";
        public static final String DATE_TIME_FORMAT_CRO = "dd.MM.yyyy HH:mm:ss";
    }

    public static class orderStatusConstValue {
        public static final int ORDER_STATUS_PENDING = 0;
        public static final int ORDER_STATUS_READY = 1;
        public static final int ORDER_STATUS_DECLINED = 2;
        public static final int ORDER_STATUS_REMOVAL_REQUEST = 3;
    }

    public static class statisticsConstValues {
        public static final String PIE_CHART_LABEL = "Pie chart data set";
        public static final int QUERY_TYPE_EMPLOYEES = 0;
        public static final int QUERY_TYPE_DRINKS = 1;
        public static final int QUERY_TYPE_TABLES = 2;
    }
}
