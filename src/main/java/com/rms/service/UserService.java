package com.rms.service;

import com.rms.model.Admin;
import com.rms.model.Manager;
import com.rms.model.Staff;
import com.rms.model.Guest;
import com.rms.model.User;
import com.rms.util.FileManager;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserService {
    private Map<String, User> users;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    public UserService() {
        users = new HashMap<>();
        File file = new File("users.dat");
        if (file.exists()) {
            loadUsers(); // Load users from the file if it exists
        }
        initializeAdmin(); // Ensure the admin user is created
    }

    // Create the admin if one does not exist
    private void initializeAdmin() {
        if (!users.containsKey(ADMIN_USERNAME)) {
            String password = ADMIN_PASSWORD;
            String hashedPassword = hashPassword(password);
            users.put(ADMIN_USERNAME, new Admin(ADMIN_USERNAME, hashedPassword));
            saveUsers(); // Save the admin user to file
        }
    }

    public boolean addUser(String staffID, String username, String password, String role, double hoursWorked, User currentUser) {
        if (users.containsKey(username)) return false;

        User newUser;
        String hashedPassword = hashPassword(password);

        switch (role.toUpperCase()) {
            case "MANAGER":
                if (!currentUser.canAddManager()) {
                    System.out.println("Only an admin can add a manager.");
                    return false;
                }
                newUser = new Manager(username, hashedPassword, staffID, hoursWorked);
                break;
            case "STAFF":
                if (!currentUser.canAddStaff()) {
                    System.out.println("Only an admin or a manager can add staff.");
                    return false;
                }
                newUser = new Staff(username, hashedPassword, staffID, hoursWorked);
                break;
            default:
                System.out.println("Invalid role. Please specify 'Manager' or 'Staff'.");
                return false;
        }

        users.put(username, newUser);
        saveUsers(); // Save to file after adding the user
        return true;
    }

    public boolean removeUser(String username, User currentUser) {
        if (!users.containsKey(username)) return false;
        User userToRemove = users.get(username);

        switch (userToRemove.getClass().getSimpleName().toUpperCase()) {
            case "MANAGER":
                if (!currentUser.canAddManager()) {
                    System.out.println("Only an admin can remove a manager.");
                    return false;
                }
                users.remove(username);
                break;
            case "STAFF":
                if (!currentUser.canAddStaff()) {
                    System.out.println("Only an admin or a manager can remove staff.");
                    return false;
                }
                users.remove(username);
                break;
            default:
                System.out.println("Invalid user, cannot remove.");
                return false;
        }
        saveUsers(); // Save to file after removing the user
        return true;
    }

    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && verifyPassword(password, user.getHashedPassword())) {
            return user;
        }
        return null;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    private void saveUsers() {
        FileManager.saveUsers(users, "users.dat");
    }

    private void loadUsers() {
        users = FileManager.loadUsers("users.dat");
    }

    public List<String> getAllUsernames() {
        return users.keySet().stream().collect(Collectors.toList());
    }


    // Get user by username
    public User getUser(String username) {
        return users.get(username);
    }

    // Create a Guest user
    public User createGuestUser() {
        return new Guest();
    }
}
