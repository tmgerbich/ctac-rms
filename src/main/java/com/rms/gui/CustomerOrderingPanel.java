package com.rms.gui;

import com.rms.enums.OrderStatus;
import com.rms.model.MenuItem;
import com.rms.model.Order;
import com.rms.service.Inventory;
import com.rms.service.Menu;
import com.rms.service.OrderService;
import com.rms.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomerOrderingPanel extends JPanel {
    private OrderService orderService;
    private Menu menu;
    private Inventory inventory;
    private User currentUser;
    private ArrayList<MenuItem> itemsInOrder;
    private JLabel messageLabel;
    private Order currentOrder;
    private Timer resetMessageTimer;
    private boolean orderCompleted;

    public CustomerOrderingPanel(Menu menu, Inventory inventory, OrderService orderService, User currentUser) {
        this.menu = menu;
        this.inventory = inventory;
        this.orderService = orderService;
        this.currentUser = currentUser;
        this.itemsInOrder = new ArrayList<>();
        this.currentOrder = null;
        this.orderCompleted = false;

        setLayout(new BorderLayout());

        // Welcome message
        messageLabel = new JLabel("Welcome to Nerd-Nook!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(messageLabel, BorderLayout.NORTH);

        // Display menu beneath the welcome message
        JTable menuTable = createMenuTable();
        add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JButton addOrderButton = new JButton("Create Order");
        addOrderButton.addActionListener(e -> handleAddOrder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addOrderButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set up a timer to refresh the order status periodically
        Timer refreshTimer = new Timer(1000, e -> refreshOrderStatus());
        refreshTimer.start();
    }

    private JTable createMenuTable() {
        String[] columnNames = {"Name", "Description", "Price"};
        Object[][] data = menu.getAllMenuItems().stream()
                .map(itemName -> {
                    MenuItem item = menu.getMenuItem(itemName);
                    return new Object[]{item.getName(), item.getDescription(), String.format("$%.2f", item.getPrice())};
                })
                .toArray(Object[][]::new);

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing of table cells
            }
        };

        table.setFillsViewportHeight(true);
        return table;
    }

    private void handleAddOrder() {
        List<MenuItem> items = new ArrayList<>();
        String name;

        AtomicBoolean shouldExit = new AtomicBoolean(false);

        JTextField nameField = new JTextField();
        JPanel namePanel = new JPanel(new GridLayout(1, 1));
        namePanel.add(nameField);
        int nameResult = JOptionPane.showConfirmDialog(null, namePanel, "Name For The Order:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (nameResult == JOptionPane.OK_OPTION) {
            name = nameField.getText();
        } else {
            name = "";
            JOptionPane.showMessageDialog(this, "Ordering Canceled.");
        }

        if (!name.isEmpty()) {
            handleAddOrder(name, items);  // Call the overloaded method to continue order creation
        }
    }

    private void handleAddOrder(String name, List<MenuItem> items) {
        AtomicBoolean shouldExit = new AtomicBoolean(false);

        while (true) {
            JComboBox<String> itemComboBox = new JComboBox<>(menu.getAllMenuItems().toArray(new String[0]));
            JTextField quantityField = new JTextField();

            int rowNumber = items.size() + 4;
            JPanel panel = new JPanel(new GridLayout(rowNumber, 2));
            panel.add(new JLabel("Item Name:"));
            panel.add(itemComboBox);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);

            if (!items.isEmpty()) {
                panel.add(new JLabel(""));
                panel.add(new JLabel("Current Order"));
                for (int i = 0; i < items.size(); i++) {
                    MenuItem item = items.get(i);
                    panel.add(new JLabel(item.getName()));

                    JButton removeButton = new JButton("Remove");
                    final int index = i;
                    removeButton.addActionListener(e -> {
                        items.remove(index);
                        Window window = SwingUtilities.getWindowAncestor(panel);
                        if (window instanceof JDialog) {
                            ((JDialog) window).dispose();
                        }
                        handleAddOrder(name, items);
                        shouldExit.set(true);
                    });
                    panel.add(removeButton);
                }
            }

            Object[] options = new Object[]{"Add", "Finish"};

            int result = JOptionPane.showOptionDialog(
                    null,
                    panel,
                    "Add Item to Order",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (result == JOptionPane.CLOSED_OPTION) {
                return; // Stop processing if user closes the dialog
            } else if (result == 0) {
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
                break;
            }
        }

        double totalAmount = calculateTotalAmount(items);
        if (totalAmount < 100) {
            if (!items.isEmpty() && !shouldExit.get()) {
                currentOrder = new Order(true, name, new ArrayList<>(items));
                orderService.addOrder(currentOrder);
                orderCompleted = false; // Reset the flag for the new order

                // Display thank-you message and start timer to reset it
                displayPreparingMessage(name);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Guests can only submit orders below $100.", "Order Limit", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displayPreparingMessage(String name) {
        messageLabel.setText("Thanks, " + name + "! Your order status is: " + currentOrder.getStatus());
        orderCompleted = false;

        if (resetMessageTimer != null) {
            resetMessageTimer.stop();
        }
    }

    private void displayCompletedMessage() {
        // Display the completed message
        messageLabel.setText("Thanks, " + currentOrder.getName() + "! Your order status is: COMPLETED");

        // Stop the refresh timer and set the orderCompleted flag
        orderCompleted = true;
        currentOrder = null;  // Ensure no further status updates occur

        if (resetMessageTimer != null) {
            resetMessageTimer.stop();
        }

        // Start a timer to reset the message to the welcome message after 5 seconds
        resetMessageTimer = new Timer(5000, e -> {
            messageLabel.setText("Welcome to Nerd-Nook!");
            resetMessageTimer.stop();  // Stop the timer once the welcome message is displayed
        });
        resetMessageTimer.setRepeats(false);
        resetMessageTimer.start();
    }

    private void refreshOrderStatus() {
        // Only refresh if an order is active and not yet completed
        if (currentOrder != null && !orderCompleted) {
            OrderStatus status = currentOrder.getStatus();
            if (status == OrderStatus.COMPLETED) {
                // Display the completed message and stop further updates
                displayCompletedMessage();
            } else {
                // Update the message with the current order status
                messageLabel.setText("Thanks, " + currentOrder.getName() + "! Your order status is: " + status);
            }
        }
    }


    private double calculateTotalAmount(List<MenuItem> items) {
        return items.stream().mapToDouble(MenuItem::getPrice).sum();
    }
}
