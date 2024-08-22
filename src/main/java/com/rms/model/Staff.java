package com.rms.model;

public class Staff extends User {

    private String staffID;
    private double hoursWorked;

    public Staff(String username, String hashedPassword, String staffID, double hoursWorked) {
        super(username, hashedPassword);
        this.staffID = staffID;
        this.hoursWorked = hoursWorked;
    }

    public String getStaffID() {
        return staffID;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    @Override
    public boolean canAddManager() {
        return false;
    }

    @Override
    public boolean canAddStaff() {
        return false;
    }
}
