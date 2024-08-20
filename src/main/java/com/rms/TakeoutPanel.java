package com.rms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TakeoutPanel extends JPanel {
    private OrderService orderService;
    private Menu menu;
    private Inventory inventory;

    public TakeoutPanel(OrderService orderService, Menu menu, Inventory inventory) {
        this.orderService = orderService;
        this.menu = menu;
        this.inventory = inventory;
        setLayout(new BorderLayout());

        JButton addOrderButton = new JButton("Add Takeout Order");
        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddTakeoutOrder();
            }
        });

        add(addOrderButton, BorderLayout.NORTH);
    }

    private void handleAddTakeoutOrder() {
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
            Order order = new Order(true, (ArrayList<MenuItem>) items);
            orderService.addOrder(order);
            JOptionPane.showMessageDialog(this, "Order placed successfully");
        }
    }
}
