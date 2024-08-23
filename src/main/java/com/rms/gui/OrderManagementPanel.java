package com.rms.gui;

import com.rms.model.MenuItem;
import com.rms.service.Inventory;
import com.rms.service.Menu;
import com.rms.model.Order;
import com.rms.service.OrderService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderManagementPanel extends JPanel {
    private Menu menu;
    private Inventory inventory;
    private OrderService orderService;
    private DefaultTableModel tableModel;
    private JTable menuTable;

    public OrderManagementPanel(Menu menu, Inventory inventory, OrderService orderService) {
        this.menu = menu;
        this.inventory = inventory;
        this.orderService = orderService;
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Order ID", "Destination", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        menuTable = new JTable(tableModel);
        updateOrderTable();

        add(new JScrollPane(menuTable), BorderLayout.CENTER);

        // Add a button to show order details
        JButton showDetailsButton = new JButton("Show Order Details");
        showDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOrderDetails();
            }
        });
        add(showDetailsButton, BorderLayout.SOUTH);

    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void updateOrderTable() {
        tableModel.setRowCount(0); // Clear the table before adding rows
        for (Order order : orderService.getActiveOrders()) {
            tableModel.addRow(new Object[]{
                    order.getOrderID(),
                    order.getTable() != null
                            ? "Table " + order.getTable().getTableName().toString() + " (Customer: " + order.getTable().getCustomerName() + ")"
                            : "Takeout - " + order.getName(),
                    order.getStatus()
            });
        }
        getOrderService().saveOrders();
    }

    private void showOrderDetails() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0) {
            int selectedOrderID = (int) tableModel.getValueAt(selectedRow, 0);
            Order selectedOrder = orderService.getOrder(selectedOrderID);

            // Create a panel to display order details
            JPanel panel = new JPanel(new GridLayout(0, 2)); // Use 0 rows to allow dynamic growth
            panel.add(new JLabel("Order Details"));
            panel.add(new JLabel(""));

            // Determine if the order is a table order or takeout
            String customerName = selectedOrder.isTakeOut() ? selectedOrder.getName() : selectedOrder.getTable().getCustomerName();

            panel.add(new JLabel("Customer Name: "));
            panel.add(new JLabel(customerName));

            // Calculate quantities for each unique item
            ArrayList<MenuItem> items = selectedOrder.getItems();
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
            panel.add(new JLabel("$" + String.format("%.2f", selectedOrder.getPrice())));
            panel.add(new JLabel("Status: "));
            panel.add(new JLabel(String.valueOf(selectedOrder.getStatus())));

            JOptionPane.showMessageDialog(this, panel, "Order Details", JOptionPane.PLAIN_MESSAGE);

            updateOrderTable();
        } else {
            JOptionPane.showMessageDialog(this, "No order selected.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}

