package com.rms.gui;

import com.rms.service.Inventory;
import com.rms.model.MenuItem;
import com.rms.model.Order;
import com.rms.service.OrderService;
import com.rms.service.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TakeoutPanel extends JPanel {
    private OrderService orderService;
    private Menu menu;
    private Inventory inventory;
    private DefaultTableModel tableModel;
    private JTable takeoutOrderTable;

    public TakeoutPanel(OrderService orderService, Menu menu, Inventory inventory) {
        this.orderService = orderService;
        this.menu = menu;
        this.inventory = inventory;
        setLayout(new BorderLayout());


        // Table setup
        String[] columnNames = {"Order ID", "Customer", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        takeoutOrderTable = new JTable(tableModel);
        updateTakeoutOrderTable();

        add(new JScrollPane(takeoutOrderTable), BorderLayout.CENTER);

        JButton addOrderButton = new JButton("Add Takeout Order");
        addOrderButton.addActionListener(e -> handleAddTakeoutOrder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addOrderButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set up a timer to refresh the table periodically
        Timer refreshTimer = new Timer(1000, e -> updateTakeoutOrderTable());
        refreshTimer.start();

    }

    public void updateTakeoutOrderTable() {
        tableModel.setRowCount(0); // Clear the table before adding rows
        for (Order order : orderService.getActiveOrders()) {
            if (order.isTakeOut()) {
                tableModel.addRow(new Object[]{
                        order.getOrderID(),
                        order.getName(),
                        order.getStatus()
                });
            }
        }
    }

    private void handleAddTakeoutOrder() {
        List<MenuItem> items = new ArrayList<>();
        String name;

        //boolean flag to keep track of if the instance of running this method is the one that should add to order at the end or if the order was added in a different recursive method call
        AtomicBoolean shouldExit = new AtomicBoolean(false);
        JTextField nameField = new JTextField();
        JPanel namePanel = new JPanel(new GridLayout(1,1));
        namePanel.add(nameField);
        int nameResult = JOptionPane.showConfirmDialog(null, namePanel, "Name For The Order:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (nameResult == JOptionPane.OK_OPTION) {
            name = nameField.getText();
        } else {
            name = "";
            JOptionPane.showMessageDialog(this, "Ordering Canceled.");
        }


        if (!name.isEmpty()) {
            while (true) {
                JComboBox<String> itemComboBox = new JComboBox<>(menu.getAllMenuItems().toArray(new String[0]));
                JTextField quantityField = new JTextField();

                // Calculate the number of rows needed for the GridLayout
                int rowNumber = items.size() + 4;
                JPanel panel = new JPanel(new GridLayout(rowNumber, 2));
                panel.add(new JLabel("Item Name:"));
                panel.add(itemComboBox);
                panel.add(new JLabel("Quantity:"));
                panel.add(quantityField);


                // Add current order items to the panel
                if (!items.isEmpty()) {
                    panel.add(new JLabel(""));
                    panel.add(new JLabel("Current Order"));
                    for (int i = 0; i < items.size(); i++) {
                        MenuItem item = items.get(i);
                        panel.add(new JLabel(item.getName()));

                        // Create a remove button for each item
                        JButton removeButton = new JButton("Remove");
                        final int index = i;  // Use final or effectively final variable for lambda
                        removeButton.addActionListener(e -> {
                            items.remove(index);
                            // Close the current dialog
                            Window window = SwingUtilities.getWindowAncestor(panel);
                            if (window instanceof JDialog) {
                                ((JDialog) window).dispose();
                            }
                            // Refresh the dialog by re-calling handleTakeoutOrder with items as a parameter using overloaded
                            handleAddTakeoutOrder(name, items);
                            shouldExit.set(true);

                        });
                        panel.add(removeButton);
                    }
                }


                // Define the options for the custom dialog
                Object[] options = new Object[]{"Add", "Finish"};

                // Show the dialog with custom options
                int result = JOptionPane.showOptionDialog(
                        null, // parent component, null for center of screen
                        panel, // component to display
                        "Add Item to Order", // title of the dialog
                        JOptionPane.DEFAULT_OPTION, // option type
                        JOptionPane.PLAIN_MESSAGE, // message type
                        null, // icon (null means no icon)
                        options, // options to display
                        options[0] // default button (first option in this case)
                );

                // Handle "Add" action
                if (result == 0) {
                    try {
                        String itemName = (String) itemComboBox.getSelectedItem();
                        int quantity = Integer.parseInt(quantityField.getText());

                        if (!itemName.isEmpty() && quantity > 0) {
                            MenuItem item = menu.getMenuItem(itemName);
                            for (int i = 0; i < quantity; i++) {
                                items.add(item);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    break;  // Exit the loop when finish is clicked
                }
            }

            if (!items.isEmpty() && !shouldExit.get()) { //this checks the boolean to see if this is the final order submission or a leftover thing still needing to finish out since return wasn't working and if so does not add the order
                // Create and process the order
                Order order = new Order(true, name, new ArrayList<>(items));
                orderService.addOrder(order); // The order will automatically be processed by the OrderProcessor
                JOptionPane.showMessageDialog(this, "Order placed successfully");
            }
            //if eventually going to add visual for the order in this tab it will get refreshed here
        }
    }

    //overloaded constructor to keep order when removing and recalling function
    private void handleAddTakeoutOrder(String name, List<MenuItem> items) {
        //boolean flag to keep track of if the instance of running this method is the one that should add to order at the end or if the order was added in a different recursive method call
        AtomicBoolean shouldExit = new AtomicBoolean(false);

        while (true) {
            JComboBox<String> itemComboBox = new JComboBox<>(menu.getAllMenuItems().toArray(new String[0]));
            JTextField quantityField = new JTextField();

            // Calculate the number of rows needed for the GridLayout
            int rowNumber = items.size() + 4;
            JPanel panel = new JPanel(new GridLayout(rowNumber, 2));
            panel.add(new JLabel("Item Name:"));
            panel.add(itemComboBox);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);

            // Add current order items to the panel
            if (!items.isEmpty()) {
                panel.add(new JLabel(""));
                panel.add(new JLabel("Current Order"));
                for (int i = 0; i < items.size(); i++) {
                    MenuItem item = items.get(i);
                    panel.add(new JLabel(item.getName()));

                    // Create a remove button for each item
                    JButton removeButton = new JButton("Remove");
                    final int index = i;  // Use final or effectively final variable for lambda
                    removeButton.addActionListener(e -> {
                        items.remove(index);
                        // Close the current dialog
                        Window window = SwingUtilities.getWindowAncestor(panel);
                        if (window instanceof JDialog) {
                            ((JDialog) window).dispose();
                        }
                        // Refresh the dialog by re-calling handleTakeoutOrder with items as a parameter using overloaded
                        handleAddTakeoutOrder(name, items);
                        shouldExit.set(true);

                    });
                    panel.add(removeButton);
                }
            }


            // Define the options for the custom dialog
            Object[] options = new Object[]{"Add", "Finish"};

            // Show the dialog with custom options
            int result = JOptionPane.showOptionDialog(
                    null, // parent component, null for center of screen
                    panel, // component to display
                    "Add Item to Order", // title of the dialog
                    JOptionPane.DEFAULT_OPTION, // option type
                    JOptionPane.PLAIN_MESSAGE, // message type
                    null, // icon (null means no icon)
                    options, // options to display
                    options[0] // default button (first option in this case)
            );

            // Handle "Add" action
            if (result == 0) {
                try {
                    String itemName = (String) itemComboBox.getSelectedItem();
                    int quantity = Integer.parseInt(quantityField.getText());

                    if (!itemName.isEmpty() && quantity > 0) {
                        MenuItem item = menu.getMenuItem(itemName);
                        for (int i = 0; i < quantity; i++) {
                            items.add(item);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;  // Exit the loop when finish is clicked
            }
        }

        if (!items.isEmpty() && !shouldExit.get()) { //this checks the boolean to see if this is the final order submission or a leftover thing still needing to finish out since return wasn't working and if so does not add the order
            // Create and process the order
            Order order = new Order(true, name, new ArrayList<>(items));
            orderService.addOrder(order); // The order will automatically be processed by the OrderProcessor
            JOptionPane.showMessageDialog(this, "Order placed successfully");
        }

        //same deal if need to update display later it goes here
    }
}