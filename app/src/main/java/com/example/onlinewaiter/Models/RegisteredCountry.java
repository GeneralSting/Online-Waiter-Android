package com.example.onlinewaiter.Models;

public class RegisteredCountry {
    String code, codeLocale, countryNumberCode, currency, dateFormat, dateTimeFormat, decimalSeperator, flag,
            mailBody, mailBodyInfo, mailSubject, name, nameLocale;

    public RegisteredCountry() {

    }

    public RegisteredCountry(String code, String codeLocale, String countryNumberCode, String currency, String dateFormat,
                             String dateTimeFormat, String decimalSeperator, String flag,
                             String mailBody, String mailBodyInfo, String mailSubject, String name, String nameLocale) {
        this.code = code;
        this.codeLocale = codeLocale;
        this.countryNumberCode = countryNumberCode;
        this.currency = currency;
        this.dateFormat = dateFormat;
        this.dateTimeFormat = dateTimeFormat;
        this.decimalSeperator = decimalSeperator;
        this.flag = flag;
        this.mailBody = mailBody;
        this.mailBodyInfo = mailBodyInfo;
        this.mailSubject = mailSubject;
        this.name = name;
        this.nameLocale = nameLocale;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeLocale() {
        return codeLocale;
    }

    public void setCodeLocale(String codeLocale) {
        this.codeLocale = codeLocale;
    }

    public String getCountryNumberCode() {
        return countryNumberCode;
    }

    public void setCountryNumberCode(String countryNumberCode) {
        this.countryNumberCode = countryNumberCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getDecimalSeperator() {
        return decimalSeperator;
    }

    public void setDecimalSeperator(String decimalSeperator) {
        this.decimalSeperator = decimalSeperator;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public String getMailBodyInfo() {
        return mailBodyInfo;
    }

    public void setMailBodyInfo(String mailBodyInfo) {
        this.mailBodyInfo = mailBodyInfo;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLocale() {
        return nameLocale;
    }

    public void setNameLocale(String nameLocale) {
        this.nameLocale = nameLocale;
    }
}