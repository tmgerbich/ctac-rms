package com.rms;

public class Manager extends User {

    public Manager(String username, String hashedPassword) {
        super(username, hashedPassword);
    }

    @Override
    public boolean canAddManager() {
        return false;
    }

    @Override
    public boolean canAddStaff() {
        return true;
    }
}