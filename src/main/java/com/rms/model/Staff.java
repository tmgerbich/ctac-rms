package com.rms.model;

public class Staff extends User {

    public Staff(String username, String hashedPassword) {
        super(username, hashedPassword);
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