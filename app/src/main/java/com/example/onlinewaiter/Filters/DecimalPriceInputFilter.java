package com.example.onlinewaiter.Filters;

import android.text.InputFilter;
import android.text.Spanned;

import com.example.onlinewaiter.Other.AppConstValue;

public class DecimalPriceInputFilter implements InputFilter {
    private final String DOT = AppConstValue.variableConstValue.DOT;

    private final int mMaxIntegerDigitsLength;
    private final int mMaxDigitsAfterLength;
    private final double mMax;

    public DecimalPriceInputFilter(int maxDigitsBeforeDot, int maxDigitsAfterDot, double maxValue) {
        mMaxIntegerDigitsLength = maxDigitsBeforeDot;
        mMaxDigitsAfterLength = maxDigitsAfterDot;
        mMax = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String allText = getAllText(source, dest, dstart);
        String onlyDigitsText = getOnlyDigitsPart(allText);

        if (allText.isEmpty()) {
            return null;
        } else {
            double enteredValue;
            try {
                enteredValue = Double.parseDouble(onlyDigitsText);
            } catch (NumberFormatException e) {
                return AppConstValue.variableConstValue.EMPTY_VALUE;
            }
            return checkMaxValueRule(enteredValue, onlyDigitsText);
        }
    }

    private CharSequence checkMaxValueRule(double enteredValue, String onlyDigitsText) {
        if (enteredValue > mMax) {
            return AppConstValue.variableConstValue.EMPTY_VALUE;
        } else {
            return handleInputRules(onlyDigitsText);
        }
    }

    private CharSequence handleInputRules(String onlyDigitsText) {
        if (isDecimalDigit(onlyDigitsText)) {
            return checkRuleForDecimalDigits(onlyDigitsText);
        } else {
            return checkRuleForIntegerDigits(onlyDigitsText.length());
        }
    }

    private boolean isDecimalDigit(String onlyDigitsText) {
        return onlyDigitsText.contains(DOT);
    }

    private CharSequence checkRuleForDecimalDigits(String onlyDigitsPart) {
        String afterDotPart = onlyDigitsPart.substring(onlyDigitsPart.indexOf(DOT), onlyDigitsPart.length() - 1);
        if (afterDotPart.length() > mMaxDigitsAfterLength) {
            return AppConstValue.variableConstValue.EMPTY_VALUE;
        }
        return null;
    }

    private CharSequence checkRuleForIntegerDigits(int allTextLength) {
        if (allTextLength > mMaxIntegerDigitsLength) {
            return AppConstValue.variableConstValue.EMPTY_VALUE;
        }
        return null;
    }

    private String getOnlyDigitsPart(String text) {
        // regex [^0-9?!\.]
        return text.replaceAll(AppConstValue.regex.FILTER_ONLY_DIGITS_PART, AppConstValue.variableConstValue.EMPTY_VALUE);
    }

    private String getAllText(CharSequence source, Spanned dest, int dstart) {
        String allText = AppConstValue.variableConstValue.EMPTY_VALUE;
        if (!dest.toString().isEmpty()) {
            if (source.toString().isEmpty()) {
                allText = deleteCharAtIndex(dest, dstart);
            } else {
                allText = new StringBuilder(dest).insert(dstart, source).toString();
            }
        }
        return allText;
    }

    private String deleteCharAtIndex(Spanned dest, int dstart) {
        StringBuilder builder = new StringBuilder(dest);
        builder.deleteCharAt(dstart);
        return builder.toString();
    }
}
