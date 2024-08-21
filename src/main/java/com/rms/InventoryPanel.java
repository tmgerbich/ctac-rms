package com.rms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryPanel extends JPanel {
    private Inventory inventory;
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JButton editButton; // Declare the editButton

    public InventoryPanel() {
        this.inventory = new Inventory();

        setLayout(new BorderLayout());

        // Set up JTable for structured display
        String[] columnNames = {"Ingredient", "Quantity", "Unit"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel);
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

    private void updateInventoryTable() {
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
                    Ingredient ingredientToAdd = new Ingredient(ingredient, quantityInt, unit);
                    boolean addSuccess = inventory.addIngredient(ingredientToAdd);
                    if (addSuccess) {
                        JOptionPane.showMessageDialog(this, "Ingredient added successfully.");
                        updateInventoryTable(); // Update JTable
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Failed to parse quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRemoveIngredient() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ingredientName = (String) tableModel.getValueAt(selectedRow, 0);

            boolean success = inventory.removeIngredient(ingredientName);
            if (success) {
                JOptionPane.showMessageDialog(this, "Ingredient removed successfully.");
                updateInventoryTable(); // Update JTable to reflect the removal
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to remove.");
        }
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
                        Ingredient editedIngredient = new Ingredient(newIngredientName, newQuantity, newUnit);
                        boolean success = inventory.updateIngredient(ingredientName, editedIngredient);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Ingredient updated successfully.");
                            updateInventoryTable(); // Update JTable
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to parse ingredient quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to edit.");
        }
    }
    private void handleRestockIngredient() {
        // Logic to restock ingredients
        // This could involve selecting an ingredient and adding a specific quantity
    }
}