package com.rms.gui;

import com.rms.model.MenuItem;
import com.rms.enums.OrderStatus;
import com.rms.enums.TableStatus;
import com.rms.model.Order;
import com.rms.model.Table;
import com.rms.service.Inventory;
import com.rms.service.Menu;
import com.rms.service.OrderService;
import com.rms.service.TableService;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TablePanel extends JPanel {
    private TableService tableService;
    private OrderService orderService;
    private Menu menu;
    private Inventory inventory;
    private Map<Table, JButton> tableButtonMap;  // Map to store table-button pairs

    public TablePanel(TableService tableService, OrderService orderService, Menu menu, Inventory inventory) {
        this.tableService = tableService;
        this.orderService = orderService;
        this.menu = menu;
        this.inventory = inventory;
        this.tableButtonMap = new HashMap<>();
        setLayout(new GridLayout(2, 3, 10, 10));  // Layout for 6 tables in 2 rows

        initializeTables();

        // Set up a timer to refresh the tables periodically
        Timer refreshTimer = new Timer(1000, e -> refreshTables());
        refreshTimer.start();
    }

    private void initializeTables() {
        for (String tableName : tableService.getAllTables()) {
            Table table = tableService.getTable(tableName);
            JButton tableButton = new JButton(getButtonLabel(table));

            // Set initial status color
            tableButton.setBackground(getColorForStatus(table.getTableStatus()));

            tableButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        handleTableClick(table, tableButton);
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int confirm = JOptionPane.showConfirmDialog(
                                TablePanel.this,
                                "Are you sure you want to clear this table?",
                                "Confirm Table Clear",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            clearTable(table, tableButton);
                        }
                    }
                }
            });

            add(tableButton);
            tableButtonMap.put(table, tableButton);  // Store the mapping between table and button
        }
    }

    public void refreshTables() {
        for (Map.Entry<Table, JButton> entry : tableButtonMap.entrySet()) {
            Table table = entry.getKey();
            JButton tableButton = entry.getValue();

            try {
                Order order = orderService.getOrderForTable(table);

                if (order != null && order.getStatus() == OrderStatus.COMPLETED) {
                    System.out.println("Order " + order.getOrderID() + " status: " + order.getStatus());
                    table.setTableStatus(TableStatus.SERVED);
                    updateTableButton(table, tableButton);
                } else if (order == null) {
                    // Handle the case where no order is found for the table, if needed
                }
            } catch (Exception e) {
                System.err.println("Error refreshing table " + table.getTableName() + ": " + e.getMessage());
                // Optionally log the stack trace for deeper debugging
                e.printStackTrace();
            }
        }
    }

    private void handleTableClick(Table table, JButton tableButton) {
        switch (table.getTableStatus()) {
            case AVAILABLE:
                assignCustomerOrReserve(table, tableButton);
                break;
            case RESERVED:
                seatCustomer(table, tableButton);
                break;
            case OCCUPIED:
                takeOrder(table, tableButton);
                break;
            case ORDERED:
            case SERVED:
                showOrderOptions(table);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid table status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOrderOptions(Table table) {
        Order order = orderService.getOrderForTable(table);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "No order found for this table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Panel to hold the message and buttons
        JPanel panel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel(table.getTableStatus() == TableStatus.ORDERED ?
                "The party has already ordered." :
                "The party has already been served.");
        panel.add(messageLabel, BorderLayout.CENTER);

        // Custom buttons for the dialog
        JButton okButton = new JButton("OK");
        JButton showOrderButton = new JButton("Show Order");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(showOrderButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Show the dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Table Status", true);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // Action for the OK button
        okButton.addActionListener(e -> dialog.dispose());

        // Action for the Show Order button
        showOrderButton.addActionListener(e -> {
            dialog.dispose(); // Close the current dialog
            showOrderDetails(order); // Show the order details
        });

        dialog.setVisible(true);
    }

    private void showOrderDetails(Order order) {
        JPanel panel = new JPanel(new GridLayout(0, 2)); // Grid layout with dynamic rows
        panel.add(new JLabel("Order Details"));
        panel.add(new JLabel(""));

        panel.add(new JLabel("Table:"));
        panel.add(new JLabel(order.getTable().getTableName()));

        // Calculate quantities for each unique item
        List<MenuItem> items = order.getItems();
        Map<MenuItem, Integer> itemQuantityMap = new HashMap<>();

        for (MenuItem item : items) {
            itemQuantityMap.put(item, itemQuantityMap.getOrDefault(item, 0) + 1);
        }

        // Add each item with its quantity to the panel
        panel.add(new JLabel("Items:"));
        panel.add(new JLabel("")); // Empty label for spacing

        for (Map.Entry<MenuItem, Integer> entry : itemQuantityMap.entrySet()) {
            MenuItem item = entry.getKey();
            int quantity = entry.getValue();
            panel.add(new JLabel(item.getName()));
            panel.add(new JLabel("Quantity: " + quantity));
        }

        panel.add(new JLabel("Price: "));
        panel.add(new JLabel("$" + String.format("%.2f", order.getPrice())));
        panel.add(new JLabel("Status: "));
        panel.add(new JLabel(String.valueOf(order.getStatus())));




        panel.add(new JLabel("")); // Empty label for spacing


        // Show the panel in a dialog
        JOptionPane.showMessageDialog(this, panel, "Order Details", JOptionPane.PLAIN_MESSAGE);
    }

    private void assignCustomerOrReserve(Table table, JButton tableButton) {
        String[] options = {"Seat Customer", "Reserve Table"};
        int choice = JOptionPane.showOptionDialog(this, "Select an action for " + table.getTableName(), "Table Action",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name:");
            if (customerName != null && !customerName.isEmpty()) {
                table.setTableStatus(TableStatus.OCCUPIED);
                table.setCustomerName(customerName);
                JOptionPane.showMessageDialog(this, "Customer seated at " + table.getTableName());
            }
        } else if (choice == 1) {
            String reservationName = JOptionPane.showInputDialog(this, "Enter Reservation Name:");
            if (reservationName != null && !reservationName.isEmpty()) {
                table.setTableStatus(TableStatus.RESERVED);
                table.setCustomerName(reservationName); // Assuming the same field is used for reservation names
                JOptionPane.showMessageDialog(this, table.getTableName() + " has been reserved for " + reservationName + ".");
            }
        }
        updateTableButton(table, tableButton);  // Update button text and color after changes
    }

    private void seatCustomer(Table table, JButton tableButton) {
        String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        if (customerName != null && !customerName.isEmpty()) {
            table.setTableStatus(TableStatus.OCCUPIED);
            table.setCustomerName(customerName);
            JOptionPane.showMessageDialog(this, "Customer seated at " + table.getTableName());
        }
        updateTableButton(table, tableButton);  // Update button text and color after changes
    }

    private void takeOrder(Table table, JButton tableButton) {
        List<MenuItem> items = new ArrayList<>();
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
                        // Refresh the dialog by re-calling takeOrder with items as a parameter using overloaded
                        takeOrder(table, tableButton, items);
                        shouldExit.set(true);
                    });
                    panel.add(removeButton);
                }
            }

            // Define the options for the custom dialog
            Object[] options = new Object[] {"Add", "Finish"};

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
            if (result == JOptionPane.CLOSED_OPTION) {
                return; // Stop processing if user closes the dialog
                // Handle "Add" action
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
            }
            else {
                break;  // Exit the loop when finish is clicked
            }
        }

        if (!items.isEmpty() && !shouldExit.get()) {
            // Create and process the order
            Order order = new Order(table, new ArrayList<>(items));
            if (orderService.subtractIngredients(inventory, order)){
            orderService.addOrder(order); // The order will automatically be processed by the OrderProcessor
            table.setTableStatus(TableStatus.ORDERED); // Change status to ordered
            JOptionPane.showMessageDialog(null, "Order placed successfully for " + table.getTableName());
        }else {
                JOptionPane.showMessageDialog(null, "Out of Stock");
            }}

        updateTableButton(table, tableButton);  // Update button text and color after changes
    }

    //overloaded constructor to keep order when removing and recalling function
    private void takeOrder(Table table, JButton tableButton, List<MenuItem> items) {
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
                        // Refresh the dialog by re-calling takeOrder using this overloaded constructor to maintain current order
                        takeOrder(table, tableButton, items);
                        shouldExit.set(true);
                    });
                    panel.add(removeButton);
                }
            }

            // Define the options for the custom dialog
            Object[] options = new Object[] {"Add", "Finish"};

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
            if (result == JOptionPane.CLOSED_OPTION) {
                return; // Stop processing if user closes the dialog
                // Handle "Add" action
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
            }
            else {
                break;  // Exit the loop when finish is clicked
            }
        }

        if (!items.isEmpty() && !shouldExit.get()) {
            // Create and process the order
            Order order = new Order(table, new ArrayList<>(items));
            if (orderService.subtractIngredients(inventory, order)){
                orderService.addOrder(order); // The order will automatically be processed by the OrderProcessor
                table.setTableStatus(TableStatus.ORDERED); // Change status to ordered
                JOptionPane.showMessageDialog(null, "Order placed successfully for " + table.getTableName());
            } else {
            JOptionPane.showMessageDialog(null, "Out of Stock");
        }}

        updateTableButton(table, tableButton);  // Update button text and color after changes
    }

    private void clearTable(Table table, JButton tableButton) {
        Order order = orderService.getOrderForTable(table);
        int orderID = order.getOrderID();
        orderService.updateOrderStatus(orderID, OrderStatus.CLEARED);
        table.setTableStatus(TableStatus.AVAILABLE);
        table.setCustomerName(null);
        JOptionPane.showMessageDialog(this, table.getTableName() + " is now cleared and available.");
        updateTableButton(table, tableButton);  // Update button text and color after changes
    }

    private void updateTableButton(Table table, JButton tableButton) {
        System.out.println("Updating button for " + table.getTableName() + " to status " + table.getTableStatus());
        tableButton.setText(getButtonLabel(table));
        tableButton.setBackground(getColorForStatus(table.getTableStatus()));
    }

    private String getButtonLabel(Table table) {
        String label = "Table " + table.getTableName();
        if (table.getTableStatus() == TableStatus.RESERVED && table.getCustomerName() != null) {
            label += " (Reserved: " + table.getCustomerName() + ")";
        } else if (table.getCustomerName() != null) {
            label += " (Customer: " + table.getCustomerName() + ")";
        }
        return label;
    }

    private Color getColorForStatus(TableStatus status) {
        switch (status) {
            case AVAILABLE:
                return Color.GREEN;
            case RESERVED:
                return Color.YELLOW;
            case OCCUPIED:
                return Color.RED;
            case ORDERED:
                return Color.PINK;
            case SERVED:
                return Color.BLUE;
            default:
                return Color.GRAY;
        }
    }
}
