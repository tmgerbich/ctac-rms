package com.rms.gui;

import com.rms.enums.OrderStatus;
import com.rms.enums.TableStatus;
import com.rms.service.Inventory;
import com.rms.model.MenuItem;
import com.rms.model.Order;
import com.rms.service.OrderService;
import com.rms.service.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        JButton clearOrderButton = new JButton("Clear Takeout Order");
        clearOrderButton.addActionListener(e -> clearTakeoutOrder());
        JButton showOrderButton = new JButton("Show Order Details");
        showOrderButton.addActionListener(e -> showTakeoutOrder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addOrderButton);
        buttonPanel.add(clearOrderButton);
        buttonPanel.add(showOrderButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set up a timer to refresh the table periodically
        Timer refreshTimer = new Timer(5000, e -> updateTakeoutOrderTable());
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
                } else {
                    break;  // Exit the loop when finish is clicked
                }
            }

            if (!items.isEmpty() && !shouldExit.get()) {
                // Create and process the order
                Order order = new Order(true, name, new ArrayList<>(items));
                if (orderService.subtractIngredients(inventory, order)){
                    orderService.addOrder(order); // The order will automatically be processed by the OrderProcessor
                    JOptionPane.showMessageDialog(this, "Order placed successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Out of Stock");
                }}
            updateTakeoutOrderTable();
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
            } else {
                break;  // Exit the loop when finish is clicked
            }
        }

        if (!items.isEmpty() && !shouldExit.get()) {
            // Create and process the order
            Order order = new Order(true, name, new ArrayList<>(items));
            if (orderService.subtractIngredients(inventory, order)){
                orderService.addOrder(order); // The order will automatically be processed by the OrderProcessor
                JOptionPane.showMessageDialog(this, "Order placed successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Out of Stock");
            }}

        updateTakeoutOrderTable();
    }

    private void clearTakeoutOrder() {
        int selectedRow = takeoutOrderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int selectedOrder = (int) tableModel.getValueAt(selectedRow, 0);
            orderService.updateOrderStatus(selectedOrder, OrderStatus.CLEARED);
            updateTakeoutOrderTable(); // Refresh the table to reflect the cleared status
        } else {
            JOptionPane.showMessageDialog(this, "No order selected.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showTakeoutOrder() {
        int selectedRow = takeoutOrderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int selectedOrder = (int) tableModel.getValueAt(selectedRow, 0);
            Order selectedOrderFull = orderService.getOrder(selectedOrder);

            // Create the panel for displaying the order details
            JPanel panel = new JPanel(new GridLayout(0, 2)); // Use 0 rows to allow dynamic growth
            panel.add(new JLabel("Order Details"));
            panel.add(new JLabel(""));
            panel.add(new JLabel("Customer Name: "));
            panel.add(new JLabel(selectedOrderFull.getName()));

            // Calculate quantities for each unique item
            List<MenuItem> items = selectedOrderFull.getItems();
            Map<MenuItem, Integer> itemQuantityMap = new HashMap<>();

            for (MenuItem item : items) {
                itemQuantityMap.put(item, itemQuantityMap.getOrDefault(item, 0) + 1);
            }

            // Add each item with its quantity to the panel
            panel.add(new JLabel("Items: "));
            panel.add(new JLabel("")); // Empty label for spacing

            for (Map.Entry<MenuItem, Integer> entry : itemQuantityMap.entrySet()) {
                MenuItem item = entry.getKey();
                int quantity = entry.getValue();
                panel.add(new JLabel(item.getName()));
                panel.add(new JLabel("Quantity: " + quantity));
            }


            panel.add(new JLabel("Price: "));
            panel.add(new JLabel("$" + String.format("%.2f", selectedOrderFull.getPrice())));
            panel.add(new JLabel("Status: "));
            panel.add(new JLabel(String.valueOf(selectedOrderFull.getStatus())));



            JOptionPane.showMessageDialog(this, panel, "Takeout Order Details", JOptionPane.PLAIN_MESSAGE);

            updateTakeoutOrderTable();
        } else {
            JOptionPane.showMessageDialog(this, "No order selected.", "Error", JOptionPane.WARNING_MESSAGE);
        }
        updateTakeoutOrderTable();
    }

}