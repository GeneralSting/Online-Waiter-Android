package com.example.onlinewaiter.Models;

public class Employee {
    String employeeCafeId, employeeGmail, employeeName, employeeLastname, employeeOib;

    public Employee() {}

    public Employee(String employeeCafeId, String employeeGmail, String employeeName, String employeeLastname, String employeeOib) {
        this.employeeCafeId = employeeCafeId;
        this.employeeGmail = employeeGmail;
        this.employeeName = employeeName;
        this.employeeLastname = employeeLastname;
        this.employeeOib = employeeOib;
    }

    public String getEmployeeCafeId() {
        return employeeCafeId;
    }

    public void setEmployeeCafeId(String employeeCafeId) {
        this.employeeCafeId = employeeCafeId;
    }

    public String getEmployeeGmail() {
        return employeeGmail;
    }

    public void setEmployeeGmail(String employeeGmail) {
        this.employeeGmail = employeeGmail;
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

    public String getEmployeeOib() {
        return employeeOib;
    }

    public void setEmployeeOib(String employeeOib) {
        this.employeeOib = employeeOib;
    }
}
