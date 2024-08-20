package com.rms;

public class Admin extends User {

    public Admin(String username, String hashedPassword) {
        super(username, hashedPassword);
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