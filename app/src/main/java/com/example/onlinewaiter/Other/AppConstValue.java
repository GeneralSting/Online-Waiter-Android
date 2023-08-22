package com.example.onlinewaiter.Other;

public class AppConstValue {
    public static class bundleConstValue {
        public static final String LOGIN_CAFE_ID = "cafeId";
        public static final String LOGIN_PHONE_NUMBER = "phoneNumber";
        public static final String REGISTERED_NUMBER_WEB = "registeredNumberWeb";
        public static final String NUMBER_MEMORY_WORD = "numberMemoryWord";
        public static final String LOGOUT_NUMBER_CHANGE = "alertNumberChange";
        public static final String CROPPER_IMAGE_DATA = "imageCropperData";
        public static final String CROPPER_IMAGE_RESULT = "imageCropperResult";
    }

    public static class sharedPreferencesValue {
        public static final String PREFERENCE_PHONE_NUMBER = "preferencePhonenumber";
        public static final String SHARED_PHONE_NUMBER = "sharedPhoneNumber";
    }

    public static class characterConstValue {
        public static final String CHARACTER_SPACING = " ";
        public static final String IMAGE_NAME_UNDERLINE = "_";
        public static final String PERCENTAGE = "%";
        public static final String FIREBASE_SLASH = "/";
    }

    public static class decimalFormatConstValue {
        public static final String PRICE_DECIMAL_FORMAT_WITH_ZERO = "0.00";
        public static final String PRICE_DECIMAL_FORMAT_NO_ZERO = "#.00";
        public static final String PRICE_DECIMAL_COMMA_NO_ZERO = ",00";
        public static final String PRICE_DECIMAL_DOT_NO_ZERO = ".00";
        public static final String DECIMAL_PERCENTAGE = "##.##%";
    }

    public static class notificationConstValue {
        public static final String NOTIFICATION_CHANNEL_ID = "ORDER_NOTIFICATION_CHANNEL";
        public static final CharSequence NOTIFICATION_CHANNEL_NAME = "notificationChannelForOrders";
        public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Waiters recieve notification when their order is ready to serve";

    }

    public static class variableConstValue {
        public static final String EMPTY_VALUE = "";
        public static final String DOT = ".";
        public static final String DEFAULT_DECIMAL_SEPERATOR = ".";
        public static final String COMMA = ",";
        public static final char CHAR_DOT = '.';
        public static final char CHAR_COMMA = ',';
        public static final String DOT_ZERO = ".0";
        public static final String ZERO_VALUE = "0";
        public static final char CHAR_ZERO_VALUE = '0';
        public static final int EXIT_TIME_DELAY = 2000;
        public static final int STATISTICS_DEFAULT_QUERY_SIZE = 100;
        public static final int TABLE_DRINK_NAME_LENGTH = 15;
        public static final int MENU_DRINK_DESCRIPTION_LENGTH = 45;
        public static final int MENU_DRINK_DESCRIPTION_LENGTH_SUBSTRING = 42;
        public static final int MAIN_PAGER_DESCRIPTION_START = 0;
        public static final int MAIN_PAGER_DESCRIPTION_END = 150;
        public static final String SQUARE_BRACKETS_OPEN = "[";
        public static final String SQUARE_BRACKETS_CLOSED = "]";
        public static final String CAMERA_EXTRAS_DATA = "data";
        public static final String OPEN_ALL_IMAGES = "image/*";
        public static final String IMAGE_FORMAT_JPG = ".jpg";
        public static final String DEFAULT_CAFE_COUNTRY_CODE = "GB";
        public static final String FAB_DRINKS_ACTION_TEXT = "Action";
        public static final float FLOAT_PRICE_ZERO = 0f;
        public static final int DRINK_PRICE_ROUND_DECIMAL_PLACE = 2;
        public static final String DRINK_QUANTITY_OVER_HUNDRED = "99+";
        public static final int MIN_TABLE_NUMBER = 1;
        public static final String LOGOUT_NUMBER_CHANGE = "LOGOUT_NUMBER_CHANGE";
        public static final int WEB_APP_REGISTERED_DEFAULT = 0;
    }

    public static class permissionConstValue {
        public static final int REQ_READING_LOGIN_OTP = 200;
        public static final int MULTIPLE_PERMISSIONS = 300;
        public static final int CAMERA_PERMISSION_CODE = 100;
        public static final int CAMERA_REQUEST_CODE = 101;
        public static final int GALLERY_REQUEST_CODE = 111;
        public static final int GALLERY_RESULT_CODE = 112;
    }

    public static class dateConstValue {
        public static final String DATE_TIME_FORMAT_DEFAULT = "yyyy_MM_dd_hh_mm_ss";
    }

    public static class orderStatusConstValue {
        public static final int ORDER_STATUS_PENDING = 0;
        public static final int ORDER_STATUS_READY = 1;
        public static final int ORDER_STATUS_DECLINED = 2;
        public static final int ORDER_STATUS_REMOVAL_REQUEST = 3;
    }

    public static class statisticsConstValue {
        public static final String PIE_CHART_LABEL = "Pie chart data set";
        public static final int QUERY_TYPE_EMPLOYEES = 0;
        public static final int QUERY_TYPE_DRINKS = 1;
        public static final int QUERY_TYPE_TABLES = 2;
    }

    public static class registeredNumberConstValue {
        public static final String NUMBER_ROLE_WAITER = "waiter";
        public static final boolean NUMBER_ALLOWED = true;
    }

    public static class emailConstValue {
        public static final int RANDOM_ORIGIN = 100000;
        public static final int RANDOM_BOUND = 999999;
    }

    public static class employeeNavigationSelected {
        public static final int EMPLOYEE_NAV_MENU = 1;
        public static final int EMPLOYEE_NAV_PENDING_ORDERS = 3;
        public static final int EMPLOYEE_NAV_OTHERS = 999;
    }

    public static class decimalPriceInputFilter {
        public static final int MAX_DIGITS_BEFORE_DOT = 6;
        public static final int MAX_DIGITS_AFTER_DOT = 2;
        public static final int MAX_VALUE = 1000000;
    }

    public static class regex {
        public static final String NON_NUMERIC_CHARACHTERS = "\\D";
        public static final String PHONE_NUMBER_VALIDATOR = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$";
        public static final String LOGIN_OTP_VALIDATOR = "(|^)\\d{6}";
        public static final String WHITESPACE_CHARACHTERS = "\\s+";
        public static final String FILTER_ONLY_DIGITS_PART = "[^\\d?!.]";
        public static final String EMAIL_ADDRESS_VALIDATOR = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    }

    public static class recyclerViewDisplayed {
        public static final int UPDATE_NON_DISPLAYED = 0;
        public static final int UPDATE_CATEGORIES_DISPLAYED = 1;
        public static final int UPDATE_DRINKS_DISPLAYED = 2;

    }

    public static class registeredNumbersRole {
        public static final String OWNER = "owner";
        public static final String WAITER = "waiter";
    }

    public static class errorSender {
        public static final int APP = 0;
        public static final int USER_EMPLOYEE = 1;
        public static final int USER_OWNER = 2;
        public static final int WEB = 3;
    }

    public static class cafeInfoClicked {
        public static final int CAFE_NAME = 0;
        public static final int CAFE_LOCATION = 1;
        public static final int CAFE_COUNTRY = 2;
        public static final int CAFE_EMAIL = 3;
    }
}
