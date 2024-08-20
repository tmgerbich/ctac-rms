package com.rms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffManagementPanel extends JPanel {
    private UserService userService;
    private User currentUser;
    private DefaultListModel<String> userModel;
    private JList<String> userList;

    public StaffManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userService = new UserService();

        setLayout(new BorderLayout());

        // User list
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        updateUserList();

        add(new JScrollPane(userList), BorderLayout.CENTER);

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

    private void updateUserList() {
        userModel.clear();
        for (String username : userService.getAllUsernames()) {
            userModel.addElement(username);
        }
    }

    private void handleAddUser() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();
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
            String password = passwordField.getText();
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean addSuccess = userService.addUser(username, password, role, currentUser);
                if (addSuccess) {
                    JOptionPane.showMessageDialog(this, "User added successfully.");
                    updateUserList();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleRemoveUser() {
        String username = userList.getSelectedValue();
        if (username == null) {
            JOptionPane.showMessageDialog(this, "No user selected.");
            return;
        }

        if (userService.removeUser(username, currentUser)) {
            JOptionPane.showMessageDialog(this, "User removed successfully.");
            updateUserList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to remove user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
