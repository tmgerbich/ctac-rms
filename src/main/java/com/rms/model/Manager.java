package com.rms.model;

public class Manager extends User {

    private String staffID;
    private double hoursWorked;

    public Manager(String username, String hashedPassword, String staffID, double hoursWorked) {
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
        return false; // Managers cannot add other managers
    }

    @Override
    public boolean canAddStaff() {
        return true; // Managers can add staff members
    }
}
