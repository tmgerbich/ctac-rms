package com.rms.gui;

import com.rms.enums.DayNewOrNot;
import com.rms.model.Day;
import com.rms.model.MenuItem;
import com.rms.model.Order;
import com.rms.service.Inventory;
import com.rms.service.OrderService;
import com.rms.service.TableService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalesPanel extends JPanel {
    private Inventory inventory;
    private TableService tableService;
    private OrderService orderService;
    private Day dayNewOrNot;
    private JTextArea reportArea;

    public SalesPanel(Inventory inventory, TableService tableService, OrderService orderService) {
        this.inventory = inventory;
        this.tableService = tableService;
        this.orderService = orderService;
        this.dayNewOrNot = new Day();

        setLayout(new BorderLayout());

        // Create the report text area
        reportArea = new JTextArea(15, 40);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false); // Make the report area non-editable
        JScrollPane scrollPane = new JScrollPane(reportArea);

        // Create buttons
        JButton saveReportButton = new JButton("Save Report");
        JButton endDayButton = new JButton("End Day");


        // Add action listeners
        saveReportButton.addActionListener(e -> saveReport());
        endDayButton.addActionListener(e -> {
            endDay();
            System.exit(0);
        });

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveReportButton);
        buttonPanel.add(endDayButton);

        // Add components to the panel
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Populate the report when the panel is created
        populateReport();
    }

    void populateReport() {
        StringBuilder reportBuilder = new StringBuilder();

        // Example of gathering and formatting data (replace with actual methods)
        reportBuilder.append("------------------------------\n");
        reportBuilder.append("Daily Sales Report\n");
        reportBuilder.append("Date: ").append(java.time.LocalDate.now()).append("\n");
        reportBuilder.append("------------------------------\n");

        double totalRevenue = orderService.getTotalRevenue(); // Example method
        reportBuilder.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n\n");

        reportBuilder.append("Most Popular Items:\n");
        List<String> popularItems = orderService.getMostPopularItems();
        for (int i = 0; i < popularItems.size(); i++) {
            reportBuilder.append(i + 1).append(". ").append(popularItems.get(i)).append("\n");
        }
        reportBuilder.append("\n");

        reportBuilder.append("Table Sales:\n");
        Map<String, Double> tableSales = orderService.getTableSales();
        int tableCount = 1;
        for (Map.Entry<String, Double> entry : tableSales.entrySet()) {
            reportBuilder.append(tableCount++).append(". ").append(entry.getKey()).append(": $")
                    .append(String.format("%.2f", entry.getValue())).append("\n");
        }
        reportBuilder.append("\n");

        reportBuilder.append("Detailed Orders:\n");
        List<Order> detailedOrders = orderService.getDetailedOrders();

        for (Order order : detailedOrders) {
            reportBuilder.append("Order ID: #").append(order.getOrderID()).append("\n");
            if (!order.isTakeOut()){
                    reportBuilder.append("Table ID: ").append(order.getTable().getTableName()).append("\n");}
            reportBuilder.append("Items:\n");

            // Group items by their name and count occurrences
            Map<String, Long> itemCounts = order.getItems().stream()
                    .collect(Collectors.groupingBy(MenuItem::getName, Collectors.counting()));

            // Display each item with its quantity and price
            for (Map.Entry<String, Long> entry : itemCounts.entrySet()) {
                String itemName = entry.getKey();
                long quantity = entry.getValue();
                double itemPrice = order.getItems().stream()
                        .filter(item -> item.getName().equals(itemName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Item not found"))
                        .getPrice();

                reportBuilder.append(" - ").append(itemName)
                        .append(": ").append(quantity)
                        .append(" ($").append(itemPrice * quantity).append(")\n");
            }

            reportBuilder.append("Total: $").append(order.getPrice()).append("\n\n");
        }


        // Set the text of the report area
        reportArea.setText(reportBuilder.toString());
    }

    private void saveReport() {
        String report = reportArea.getText();
        if (!report.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("sales_report.txt"))) {
                writer.write(report);
                JOptionPane.showMessageDialog(this, "Sales report saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving sales report.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No report data to save.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void endDay() {
        dayNewOrNot.setNewOrNot(DayNewOrNot.NEW);
        dayNewOrNot.saveDayNewOrNot();
    }
}
