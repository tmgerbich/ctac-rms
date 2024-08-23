package com.rms.gui;

import com.rms.model.User;
import com.rms.service.Inventory;
import com.rms.service.Menu;
import com.rms.service.OrderService;
import com.rms.service.TableService;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
        this.tableService = new TableService(true); // Fix this later, hardcoding a new day
        this.orderService = new OrderService(true); // Fix this later, hardcoding a new day
        this.menu = new Menu();
        this.inventory = new Inventory();

        setTitle("Restaurant Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        InventoryPanel inventoryPanel = new InventoryPanel(inventory);
        TablePanel tablePanel = new TablePanel(tableService, orderService, menu, inventory);
        TakeoutPanel takeoutPanel = new TakeoutPanel(orderService, menu, inventory);
        MenuManagementPanel menuPanel = new MenuManagementPanel(menu, inventory);
        OrderManagementPanel orderPanel = new OrderManagementPanel(menu, inventory, orderService);
        CustomerOrderingPanel customerPanel = new CustomerOrderingPanel(menu, inventory, orderService, currentUser);

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

        // Check if the current user is a guest
        boolean isGuest = currentUser instanceof com.rms.model.Guest;

        // Add the tabs based on user role
        if (!isGuest) {
            tabbedPane.addTab("Staff Management", new StaffManagementPanel(currentUser));
            tabbedPane.addTab("Menu Management", menuPanel);
            tabbedPane.addTab("Inventory", inventoryPanel);
            tabbedPane.addTab("Table Orders", tablePanel);
            tabbedPane.addTab("Takeout Orders", takeoutPanel);
            tabbedPane.addTab("Orders", orderPanel);
        }


        tabbedPane.addTab("Guest Ordering", customerPanel);

        // Add ChangeListener to the tabbed pane
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                switch (selectedIndex) {
                    case 1: // Menu Management Tab
                        if (!isGuest) tablePanel.refreshTables();
                        break;
                    case 2: // Inventory Tab
                        if (!isGuest) inventoryPanel.updateInventoryTable();
                    case 3: // Table Orders tab
                        if (!isGuest) tablePanel.refreshTables();
                        break;
                    case 4: // Takeout Orders tab
                        if (!isGuest) takeoutPanel.updateTakeoutOrderTable();
                        break;
                    case 5: // Order Management tab
                        orderPanel.updateOrderTable();
                        break;
                    case 6: // Customer tab
                        break;
                    // Add cases for other tabs if needed
                }
            }
        });

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
