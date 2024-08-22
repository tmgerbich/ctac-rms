package com.rms.gui;

import com.rms.service.Inventory;
import com.rms.service.Menu;
import com.rms.model.Order;
import com.rms.service.OrderService;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

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
    }

}
