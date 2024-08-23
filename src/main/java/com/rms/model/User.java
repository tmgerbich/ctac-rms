package com.rms.model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private String username;
    private String hashedPassword;


    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public abstract boolean canAddManager();
    public abstract boolean canAddStaff();
}