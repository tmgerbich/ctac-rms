package com.rms.gui;

import com.rms.model.Admin;
import com.rms.model.Manager;
import com.rms.model.Staff;
import com.rms.model.User;
import com.rms.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StaffManagementPanel extends JPanel {
    private UserService userService;
    private User currentUser;

    private DefaultTableModel tableModel;
    private JTable userTable;

    public StaffManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userService = new UserService();

        setLayout(new BorderLayout());

        // Update User table setup to include staffID and hoursWorked
        String[] columnNames = {"Staff ID", "Username", "Role", "Hours Worked"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        updateUserTable(); // Populate JTable

        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton removeButton = new JButton("Remove User");
        JButton editButton = new JButton("Edit User");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(editButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(this::handleAddUser);
        removeButton.addActionListener(this::handleRemoveUser);
        editButton.addActionListener(this::handleEditUser);
    }

    private List<String> getSortedUsernames() {
        List<String> usernames = new ArrayList<>(userService.getAllUsernames());
        usernames.remove("admin");
        Collections.sort(usernames);
        usernames.add(0, "admin");
        return usernames;
    }

    private void updateUserTable() {
        List<String> sortedUsernames = getSortedUsernames();
        tableModel.setRowCount(0); // Clear the table before adding rows
        for (String username : sortedUsernames) {
            User user = userService.getUser(username);
            String role = user instanceof Admin ? "Admin" : user instanceof Manager ? "Manager" : "Staff";
            String staffID = user instanceof Staff ? ((Staff) user).getStaffID() : user instanceof Manager ? ((Manager) user).getStaffID() : "N/A";
            double hoursWorked = user instanceof Staff ? ((Staff) user).getHoursWorked() : user instanceof Manager ? ((Manager) user).getHoursWorked() : 0;
            tableModel.addRow(new Object[]{staffID, username, role, hoursWorked});
        }
    }

    private void handleAddUser(ActionEvent e) {
        JTextField staffIDField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Manager", "Staff"});
        JTextField hoursWorkedField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Staff ID:"));
        panel.add(staffIDField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);
        panel.add(new JLabel("Hours Worked:"));
        panel.add(hoursWorkedField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String staffID = staffIDField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            String hoursWorkedStr = hoursWorkedField.getText();

            if (username.isEmpty() || password.isEmpty() || role == null || staffID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    double hoursWorked = Double.parseDouble(hoursWorkedStr);

                    boolean addSuccess = userService.addUser(staffID, username, password, role, hoursWorked, currentUser);
                    if (addSuccess) {
                        JOptionPane.showMessageDialog(this, "User added successfully.");
                        updateUserTable(); // Update JTable
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid hours worked. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleRemoveUser(ActionEvent e) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1);
            if ("admin".equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, "Admin cannot be removed.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove the user: " + username + "?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = userService.removeUser(username, currentUser);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User removed successfully.");
                    updateUserTable(); // Update JTable
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to remove.");
        }
    }

    private void handleEditUser(ActionEvent e) {
        // Check if the current user is allowed to edit (must be Admin or Manager)
        if (!(currentUser instanceof Admin || currentUser instanceof Manager)) {
            JOptionPane.showMessageDialog(this, "Only Managers or Admins can edit user information.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1);
            User user = userService.getUser(username);

            JTextField staffIDField = new JTextField(user instanceof Staff || user instanceof Manager ? ((Staff) user).getStaffID() : "");
            JTextField usernameField = new JTextField(username);
            JTextField hoursWorkedField = new JTextField(user instanceof Staff || user instanceof Manager ? String.valueOf(((Staff) user).getHoursWorked()) : "0");

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Staff ID:"));
            panel.add(staffIDField);
            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Hours Worked:"));
            panel.add(hoursWorkedField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newStaffID = staffIDField.getText();
                String newUsername = usernameField.getText();
                String hoursWorkedStr = hoursWorkedField.getText();

                if (newUsername.isEmpty() || newStaffID.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        double newHoursWorked = Double.parseDouble(hoursWorkedStr);

                        boolean removeSuccess = userService.removeUser(username, currentUser);
                        if (removeSuccess) {
                            boolean addSuccess = userService.addUser(newStaffID, newUsername, user.getHashedPassword(), user instanceof Manager ? "Manager" : "Staff", newHoursWorked, currentUser);
                            if (addSuccess) {
                                JOptionPane.showMessageDialog(this, "User edited successfully.");
                                updateUserTable(); // Update JTable
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to edit user.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to edit user.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid hours worked. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }
}
