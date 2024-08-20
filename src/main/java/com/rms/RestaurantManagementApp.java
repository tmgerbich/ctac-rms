package com.rms;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RestaurantManagementApp extends JFrame {
    private User currentUser;
    private TableService tableService;
    private OrderService orderService;
    private Menu menu;
    private Inventory inventory;


    public RestaurantManagementApp(User currentUser) {
        this.currentUser = currentUser;
        this.tableService = new TableService(false); //fix this later, hard coding in not a new day
        this.orderService = new OrderService(false); //fix this later, hard coding in not a new day
        this.menu = new Menu();
        this.inventory = new Inventory();
        setTitle("Restaurant Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        TablePanel tablePanel = new TablePanel(tableService, orderService, menu, inventory);
        TakeoutPanel takeoutPanel = new TakeoutPanel(orderService, menu, inventory);
        MenuManagementPanel menuPanel = new MenuManagementPanel(menu, inventory);

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
        tabbedPane.addTab("Table Orders", tablePanel);
        tabbedPane.addTab("Takeout Orders", takeoutPanel);
        tabbedPane.addTab("Menu Management", menuPanel);


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
