package com.rms;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RestaurantManagementApp extends JFrame {
    private User currentUser;

    public RestaurantManagementApp(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Restaurant Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a menu bar with a logout option
        JMenuBar menuBar = new JMenuBar();
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutMenuItem = new JMenuItem("Logout");

        logoutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        accountMenu.add(logoutMenuItem);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Add the Staff Management panel
        tabbedPane.addTab("Staff Management", new StaffManagementPanel(currentUser));
        tabbedPane.addTab("Inventory", new InventoryPanel());

        // Add other panels here as needed in the future (e.g., Menu Management, Order Processing)

        add(tabbedPane);
    }

    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            // Return to the login screen
            new LoginFrame().setVisible(true);
            dispose(); // Close the current window
        }
    }
}
