package com.rms;

import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Restaurant Management System!");

        User currentUser = null;
        while (currentUser == null) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            currentUser = userService.authenticate(username, password);

            if (currentUser == null) {
                System.out.println("Invalid username or password. Please try again.");
            }
        }

        System.out.println("Login successful! You are logged in as " + currentUser.getClass().getSimpleName());

        boolean running = true;
        while (running) {
            System.out.println("\nOptions:");
            System.out.println("1. Add User");
            System.out.println("2. Delete User");
            System.out.println("3. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter new username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    System.out.print("Enter role (Manager, Staff): ");
                    String role = scanner.nextLine();

                    boolean addSuccess = userService.addUser(newUsername, newPassword, role, currentUser);
                    if (addSuccess) {
                        System.out.println("User added successfully.");
                    } else {
                        System.out.println("Failed to add user.");
                    }
                    break;

                case 2:
                    System.out.print("Enter user to be removed: ");
                    String usernameToRemove = scanner.nextLine();
                    boolean removeSuccess = userService.removeUser(usernameToRemove, currentUser);
                    if (removeSuccess) {
                        System.out.println("User removed successfully.");
                    } else {
                        System.out.println("Failed to remove user.");
                    }
                    break;

                case 3:
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }
}
