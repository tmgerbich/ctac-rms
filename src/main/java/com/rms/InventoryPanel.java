package com.rms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryPanel extends JPanel {
    private Inventory inventory;
    private User currentUser;
    private DefaultListModel<String> ingredientModel;
    private JList<String> ingredientList;

    public InventoryPanel() {
        this.inventory = new Inventory();

        setLayout(new BorderLayout());

        // User list
        ingredientModel = new DefaultListModel<>();
        ingredientList = new JList<>(ingredientModel);
        updateIngredientList();

        add(new JScrollPane(ingredientList), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Ingredient");
        JButton removeButton = new JButton("Remove Ingredient");
        JButton restockButton = new JButton("Restock Ingredient");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(restockButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddIngredient();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRemoveIngredient();
            }
        });
    }

    private void updateIngredientList() {
        ingredientModel.clear();
        for (String ingredient : inventory.getAllIngredients()) {
            ingredientModel.addElement(ingredient);
        }
    }

    private void handleAddIngredient() {
        JTextField ingredientField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField unitField = new JTextField();


        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Ingredient:"));
        panel.add(ingredientField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Ingredient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ingredient = ingredientField.getText();
            String quantity = quantityField.getText();
            String unit = unitField.getText();

            int quantityInt = Integer.parseInt(quantity);

            if (ingredient.isEmpty() || quantityInt<=0 || unit.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Ingredient ingredientToAdd = new Ingredient(ingredient, quantityInt, unit);
                boolean addSuccess = inventory.addIngredient(ingredientToAdd);
                if (addSuccess) {
                    JOptionPane.showMessageDialog(this, "Ingredient added successfully.");
                    updateIngredientList();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleRemoveIngredient() {
        String ingredient = ingredientList.getSelectedValue();
        if (ingredient == null) {
            JOptionPane.showMessageDialog(this, "No ingredient selected.");
            return;
        }

        int quantity = 1;

        if (inventory.removeIngredient(ingredient, quantity)) {
            JOptionPane.showMessageDialog(this, "Ingredient removed successfully.");
            updateIngredientList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to remove ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
