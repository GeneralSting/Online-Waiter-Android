package com.example.onlinewaiter.Models;

import java.util.Date;

public class CafeEmployee {
    String employeeId, employeeName, employeeLastname, employeeGmail, employeeGender;
    Date employeeBirthDate;

    public CafeEmployee() {}

    public CafeEmployee(String employeeId, String employeeName, String employeeLastname, String employeeGmail, String employeeGender, Date employeeBirthDate) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeLastname = employeeLastname;
        this.employeeGmail = employeeGmail;
        this.employeeGender = employeeGender;
        this.employeeBirthDate = employeeBirthDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeLastname() {
        return employeeLastname;
    }

    public void setEmployeeLastname(String employeeLastname) {
        this.employeeLastname = employeeLastname;
    }

    public String getEmployeeGmail() {
        return employeeGmail;
    }

    public void setEmployeeGmail(String employeeGmail) {
        this.employeeGmail = employeeGmail;
    }

    public String getEmployeeGender() {
        return employeeGender;
    }

    public void setEmployeeGender(String employeeGender) {
        this.employeeGender = employeeGender;
    }

    public Date getEmployeeBirthDate() {
        return employeeBirthDate;
    }

    public void setEmployeeBirthDate(Date employeeBirthDate) {
        this.employeeBirthDate = employeeBirthDate;
    }
}
