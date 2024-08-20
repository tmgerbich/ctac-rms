package com.rms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TablePanel extends JPanel {
    private TableService tableService;
    private OrderService orderService;
    private Menu menu;
    private Inventory inventory;

    public TablePanel(TableService tableService, OrderService orderService, Menu menu, Inventory inventory) {
        this.tableService = tableService;
        this.orderService = orderService;
        this.menu = menu;
        this.inventory = inventory;
        setLayout(new GridLayout(2, 3, 10, 10));  // Layout for 6 tables in 2 rows

        initializeTables();
    }

    private void initializeTables() {
        for (String tableName : tableService.getAllTables()) {
            JButton tableButton = new JButton("Table " + tableName);
            Table table = tableService.getTable(tableName);

            // Set initial status
            tableButton.setBackground(getColorForStatus(table.getTableStatus()));

            // Add action listener to handle clicks
            tableButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleTableClick(table);
                    tableButton.setBackground(getColorForStatus(table.getTableStatus()));  // Update color after changes
                }
            });

            add(tableButton);
        }
    }

    private void handleTableClick(Table table) {
        if (table.getTableStatus() == TableStatus.AVAILABLE) {
            assignCustomerOrReserve(table);
        } else if (table.getTableStatus() == TableStatus.OCCUPIED) {
            takeOrder(table);
        } else {
            JOptionPane.showMessageDialog(this, "Table is currently reserved.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignCustomerOrReserve(Table table) {
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
            table.setTableStatus(TableStatus.RESERVED);
            JOptionPane.showMessageDialog(this, table.getTableName() + " has been reserved.");
        }
    }

    private void takeOrder(Table table) {
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
            orderService.addOrder(order);
            JOptionPane.showMessageDialog(this, "Order placed successfully for " + table.getTableName());
        }
    }

    private Color getColorForStatus(TableStatus status) {
        switch (status) {
            case AVAILABLE:
                return Color.GREEN;
            case RESERVED:
                return Color.YELLOW;
            case OCCUPIED:
                return Color.RED;
            default:
                return Color.GRAY;
        }
    }
}
