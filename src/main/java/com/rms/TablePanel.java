package com.rms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SwingUtilities.invokeLater(() -> {
            for (Map.Entry<Table, JButton> entry : tableButtonMap.entrySet()) {
                Table table = entry.getKey();
                JButton tableButton = entry.getValue();
                Order order = orderService.getOrderForTable(table);

                if (order != null && order.getStatus() == OrderStatus.COMPLETED) {
                    System.out.println("Order " + order.getOrderID() + " status: " + order.getStatus());
                    table.setTableStatus(TableStatus.SERVED);
                    updateTableButton(table, tableButton);
                }
            }
        });
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
                JOptionPane.showMessageDialog(this, "The party has already ordered.", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            case SERVED:
                JOptionPane.showMessageDialog(this, "The party has already been served.", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid table status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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

        while (true) {
            JComboBox<String> itemComboBox = new JComboBox<>(menu.getAllMenuItems().toArray(new String[0]));
            JTextField quantityField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Item Name:"));
            panel.add(itemComboBox);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add Item to Order", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String itemName = (String) itemComboBox.getSelectedItem();
                int quantity = Integer.parseInt(quantityField.getText());

                if (!itemName.isEmpty() && quantity > 0) {
                    MenuItem item = menu.getMenuItem(itemName);
                    for (int i = 0; i < quantity; i++) {
                        items.add(item);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;  // Exit the loop when cancel is clicked
            }
        }

        if (!items.isEmpty()) {
            Order order = new Order(table, (ArrayList<MenuItem>) items);
            orderService.addOrder(order); // The order will automatically be processed by the OrderProcessor
            table.setTableStatus(TableStatus.ORDERED); // Change status to ordered
            JOptionPane.showMessageDialog(this, "Order placed successfully for " + table.getTableName());
        }
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
                return Color.ORANGE;
            case SERVED:
                return Color.BLUE;
            default:
                return Color.GRAY;
        }
    }
}
