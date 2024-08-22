package com.rms.model;

public class Guest extends User {

    public Guest() {
        super("Guest", ""); // No password for guest user
    }

    @Override
    public boolean canAddManager() {
        return false; // Guests cannot add managers
    }

    @Override
    public boolean canAddStaff() {
        return false; // Guests cannot add staff
    }
}
