package com.rms.gui;

import com.rms.model.Ingredient;
import com.rms.service.Inventory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class InventoryPanel extends JPanel {
    private Inventory inventory;
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JButton editButton; // Declare the editButton

    public InventoryPanel(Inventory inventory) {
        this.inventory = inventory;

        setLayout(new BorderLayout());

        // Set up JTable for structured display
        String[] columnNames = {"Ingredient", "Quantity", "Unit"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int quantity = (int) getModel().getValueAt(row, 1);

                if (quantity < 20) {
                    component.setBackground(Color.YELLOW); // Highlight the row in yellow if quantity < 20
                } else {
                    component.setBackground(Color.WHITE); // Default background color
                }

                return component;
            }
        };;
        updateInventoryTable(); // Populate JTable

        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Ingredient");
        JButton removeButton = new JButton("Remove Ingredient");
        JButton restockButton = new JButton("Restock Ingredient");
        editButton = new JButton("Edit Ingredient"); // Initialize the editButton

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(restockButton);
        buttonPanel.add(editButton); // Add the editButton to the panel
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> handleAddIngredient());

        removeButton.addActionListener(e -> handleRemoveIngredient());

        restockButton.addActionListener(e -> handleRestockIngredient());

        editButton.addActionListener(e -> handleEditIngredient()); // Set action for editButton
    }

    public void updateInventoryTable() {
        tableModel.setRowCount(0); // Clear the table before adding rows
        for (Ingredient ingredient : inventory.getAllIngredientsDetailed()) {
            tableModel.addRow(new Object[]{ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit()});
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

            try {
                int quantityInt = Integer.parseInt(quantity);

                if (ingredient.isEmpty() || quantityInt <= 0 || unit.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Confirmation message before finalizing the addition
                    int confirmResult = JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to add the ingredient '" + ingredient + "' with quantity '" + quantityInt + "' and unit '" + unit + "'?",
                            "Confirm Addition",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (confirmResult == JOptionPane.YES_OPTION) {
                        Ingredient ingredientToAdd = new Ingredient(ingredient, quantityInt, unit);
                        boolean addSuccess = inventory.addIngredient(ingredientToAdd);
                        if (addSuccess) {
                            JOptionPane.showMessageDialog(this, "Ingredient added successfully.");
                            updateInventoryTable(); // Update JTable
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to add ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Addition operation cancelled.");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Failed to parse quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateInventoryTable();
    }

    private void handleRemoveIngredient() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ingredientName = (String) tableModel.getValueAt(selectedRow, 0);

            // Confirmation message before finalizing the removal
            int confirmResult = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove the ingredient '" + ingredientName + "'?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmResult == JOptionPane.YES_OPTION) {
                boolean success = inventory.removeIngredient(ingredientName);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Ingredient removed successfully.");
                    updateInventoryTable(); // Update JTable to reflect the removal
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Removal operation cancelled.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to remove.");
        }
        updateInventoryTable();
    }

    private void handleEditIngredient() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ingredientName = (String) tableModel.getValueAt(selectedRow, 0);
            Integer quantity = (Integer) tableModel.getValueAt(selectedRow, 1);
            String unit = (String) tableModel.getValueAt(selectedRow, 2);

            JTextField ingredientField = new JTextField(ingredientName);
            JTextField quantityField = new JTextField(quantity != null ? quantity.toString() : "");
            JTextField unitField = new JTextField(unit);

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Ingredient:"));
            panel.add(ingredientField);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);
            panel.add(new JLabel("Unit:"));
            panel.add(unitField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Ingredient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newIngredientName = ingredientField.getText();
                String newQuantityStr = quantityField.getText();
                String newUnit = unitField.getText();

                try {
                    int newQuantity = Integer.parseInt(newQuantityStr);

                    if (newIngredientName.isEmpty() || newQuantity <= 0 || newUnit.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Confirmation message before finalizing the update
                        int confirmResult = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to update the ingredient?\n\n" +
                                        "Old Ingredient: " + ingredientName + "\n" +
                                        "New Ingredient: " + newIngredientName + "\n\n" +
                                        "Old Quantity: " + quantity + "\n" +
                                        "New Quantity: " + newQuantity + "\n\n" +
                                        "Old Unit: " + unit + "\n" +
                                        "New Unit: " + newUnit,
                                "Confirm Update",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (confirmResult == JOptionPane.YES_OPTION) {
                            Ingredient editedIngredient = new Ingredient(newIngredientName, newQuantity, newUnit);
                            boolean success = inventory.updateIngredient(ingredientName, editedIngredient);
                            if (success) {
                                JOptionPane.showMessageDialog(this, "Ingredient updated successfully.");
                                updateInventoryTable(); // Update JTable
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to update ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Update operation cancelled.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to parse ingredient quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to edit.");
        }
        updateInventoryTable();
    }


    private void handleRestockIngredient() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ingredientName = (String) tableModel.getValueAt(selectedRow, 0);
            Integer currentQuantity = (Integer) tableModel.getValueAt(selectedRow, 1);

            JTextField restockField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Ingredient:"));
            panel.add(new JLabel(ingredientName));
            panel.add(new JLabel("Current Quantity:"));
            panel.add(new JLabel(currentQuantity.toString())); // Display the current quantity
            panel.add(new JLabel("Quantity to Add:"));
            panel.add(restockField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Restock Ingredient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String restockAmountStr = restockField.getText();

                try {
                    int restockAmount = Integer.parseInt(restockAmountStr);

                    if (restockAmount <= 0) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid quantity to restock.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        int newQuantity = currentQuantity + restockAmount;

                        // Confirmation message
                        int confirmResult = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to update the quantity from " + currentQuantity + " to " + newQuantity + "?",
                                "Confirm Update",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (confirmResult == JOptionPane.YES_OPTION) {
                            // Update the ingredient quantity in the inventory
                            Ingredient ingredient = inventory.getIngredient(ingredientName);
                            ingredient.setQuantity(newQuantity);
                            boolean success = inventory.updateIngredient(ingredientName, ingredient);

                            if (success) {
                                JOptionPane.showMessageDialog(this, "Ingredient restocked successfully.");
                                updateInventoryTable(); // Update JTable
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to restock ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Restock operation cancelled.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to parse restock quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to restock.");
        }
        updateInventoryTable();
    }
}