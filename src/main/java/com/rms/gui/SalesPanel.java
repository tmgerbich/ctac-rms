package com.rms.gui;

import com.rms.enums.DayNewOrNot;
import com.rms.model.Day;
import com.rms.service.Inventory;
import com.rms.service.OrderService;
import com.rms.service.TableService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SalesPanel extends JPanel {
    private Inventory inventory;
    private TableService tableService;
    private OrderService orderService;
    private Day dayNewOrNot;

    public SalesPanel(Inventory inventory, TableService tableService, OrderService orderService) {
        this.inventory = inventory;
        this.tableService = tableService;
        this.orderService = orderService;
        this.dayNewOrNot = new Day();

        setLayout(new BorderLayout());

        // Create a button to end the day
        JButton endDayButton = new JButton("End Day");
        endDayButton.setFont(new Font("Arial", Font.BOLD, 16));
        endDayButton.setForeground(Color.WHITE);
        endDayButton.setBackground(Color.RED);

        // Add an action listener to the button to handle the end of day
        endDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endDay();
                System.exit(0); // Close the entire GUI
            }
        });

        // Add the button to the panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(endDayButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    public void endDay() {
        dayNewOrNot.setNewOrNot(DayNewOrNot.NEW);
        dayNewOrNot.saveDayNewOrNot();
    }
}
