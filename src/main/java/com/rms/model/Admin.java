package com.rms.model;

public class Admin extends User {
    private String staffId;

    public Admin(String username, String hashedPassword) {
        super(username, hashedPassword);
        this.staffId = "0";
    }

    @Override
    public boolean canAddManager() {
        return true;
    }

    @Override
    public boolean canAddStaff() {
        return true;
    }
}