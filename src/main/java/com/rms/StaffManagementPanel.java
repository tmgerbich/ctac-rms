package com.rms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StaffManagementPanel extends JPanel {
    private UserService userService;
    private User currentUser;

    // JTable for structured display
    private DefaultTableModel tableModel;
    private JTable userTable;

    public StaffManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userService = new UserService();

        setLayout(new BorderLayout());

        // User table for structured display
        String[] columnNames = {"Username", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        updateUserTable(); // Populate JTable

        // Add JTable to the panel
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton removeButton = new JButton("Remove User");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddUser();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRemoveUser();
            }
        });
    }

    private List<String> getSortedUsernames() {
        List<String> usernames = new ArrayList<>(userService.getAllUsernames());

        // Sort the usernames, but ensure "admin" is always at the top
        usernames.remove("admin");
        Collections.sort(usernames);
        usernames.add(0, "admin");

        return usernames;
    }

    private void updateUserTable() {
        List<String> sortedUsernames = getSortedUsernames(); // Get the sorted usernames

        tableModel.setRowCount(0); // Clear the table before adding rows
        for (String username : sortedUsernames) {
            User user = userService.getUser(username); // Fetch user details directly
            String role;

            // Determine the role based on the user type
            if (user instanceof Admin) {
                role = "Admin";
            } else if (user instanceof Manager) {
                role = "Manager";
            } else {
                role = "Staff";
            }

            // Add row to the table
            tableModel.addRow(new Object[]{username, role});
        }
    }

    private void handleAddUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Manager", "Staff"});

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean addSuccess = userService.addUser(username, password, role, currentUser);
                if (addSuccess) {
                    JOptionPane.showMessageDialog(this, "User added successfully.");
                    updateUserTable(); // Update JTable
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleRemoveUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 0);
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
}


//    private void handleRemoveUser() {
//        String username = userList.getSelectedValue();
//        if (username == null) {
//            JOptionPane.showMessageDialog(this, "No user selected.");
//            return;
//        }
//
//        if (userService.removeUser(username, currentUser)) {
//            JOptionPane.showMessageDialog(this, "User removed successfully.");
//            updateUserList();
//        } else {
//            JOptionPane.showMessageDialog(this, "Failed to remove user.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//}