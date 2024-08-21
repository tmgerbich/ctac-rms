package com.rms;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class MenuManagementPanel extends JPanel {
    private Menu menu;
    private Inventory inventory;
    private DefaultTableModel tableModel;
    private JTable menuTable;

    public MenuManagementPanel(Menu menu, Inventory inventory) {
        this.menu = menu;
        this.inventory = inventory;
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Name", "Description", "Price", "Prep Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        menuTable = new JTable(tableModel);
        updateMenuTable();

        add(new JScrollPane(menuTable), BorderLayout.CENTER);

        // Buttons for add, edit, and delete actions
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Menu Item");
        JButton editButton = new JButton("Edit Menu Item");
        JButton deleteButton = new JButton("Delete Menu Item");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> handleAddMenuItem());
        editButton.addActionListener(e -> handleEditMenuItem());
        deleteButton.addActionListener(e -> handleDeleteMenuItem());
    }

    private void updateMenuTable() {
        tableModel.setRowCount(0); // Clear the table before adding rows
        for (String itemName : menu.getAllMenuItems()) {
            MenuItem item = menu.getMenuItem(itemName);
            tableModel.addRow(new Object[]{
                    item.getName(),
                    item.getDescription(),
                    item.getPrice(),
                    formatDuration(item.getPrepTime())
            });
        }
    }

    private void handleAddMenuItem() {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField prepTimeField = new JTextField();
        JComboBox<String> ingredientsComboBox1 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
        JComboBox<String> ingredientsComboBox2 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
        JComboBox<String> ingredientsComboBox3 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
        JComboBox<String> ingredientsComboBox4 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
        JTextField quantityField1 = new JTextField();
        JTextField quantityField2 = new JTextField();
        JTextField quantityField3 = new JTextField();
        JTextField quantityField4 = new JTextField();

        JPanel panel = new JPanel(new GridLayout(12, 4));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Prep Time (in seconds):"));
        panel.add(prepTimeField);
        panel.add(new JLabel("Ingredient 1:"));
        panel.add(ingredientsComboBox1);
        panel.add(new JLabel("Quantity 1:"));
        panel.add(quantityField1);
        panel.add(new JLabel("Ingredient 2:"));
        panel.add(ingredientsComboBox2);
        panel.add(new JLabel("Quantity 2:"));
        panel.add(quantityField2);
        panel.add(new JLabel("Ingredient 3:"));
        panel.add(ingredientsComboBox3);
        panel.add(new JLabel("Quantity 3:"));
        panel.add(quantityField3);
        panel.add(new JLabel("Ingredient 4:"));
        panel.add(ingredientsComboBox4);
        panel.add(new JLabel("Quantity 4:"));
        panel.add(quantityField4);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Menu Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            Duration prepTime = Duration.ofSeconds(Long.parseLong(prepTimeField.getText()));

            // Prepare the ingredients list
            List<Ingredient> ingredients = new ArrayList<>();
            addIngredientToList(ingredients, ingredientsComboBox1, quantityField1);
            addIngredientToList(ingredients, ingredientsComboBox2, quantityField2);
            addIngredientToList(ingredients, ingredientsComboBox3, quantityField3);
            addIngredientToList(ingredients, ingredientsComboBox4, quantityField4);

            MenuItem newItem = new MenuItem(name, description, prepTime, price, new ArrayList<>(ingredients));
            if (menu.addMenuItem(newItem)) {
                JOptionPane.showMessageDialog(this, "Menu item added successfully.");
                updateMenuTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add menu item. It might already exist.");
            }
        }
    }

    private void addIngredientToList(List<Ingredient> ingredients, JComboBox<String> ingredientComboBox, JTextField quantityField) {
        String ingredientName = (String) ingredientComboBox.getSelectedItem();
        Ingredient currentIngredient = inventory.getIngredient(ingredientName);
        String quantityText = quantityField.getText();
        if (ingredientName != null && !quantityText.isEmpty()) {
            int quantity = Integer.parseInt(quantityText);
            String unit = currentIngredient.getUnit();
            Ingredient ingredient = new Ingredient(ingredientName, quantity, unit);
            ingredients.add(ingredient);
        }
    }

    private void handleEditMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0) {
            String selectedName = (String) tableModel.getValueAt(selectedRow, 0);
            MenuItem currentItem = menu.getMenuItem(selectedName);

            JTextField nameField = new JTextField(currentItem.getName());
            JTextField descriptionField = new JTextField(currentItem.getDescription());
            JTextField priceField = new JTextField(String.valueOf(currentItem.getPrice()));
            JTextField prepTimeField = new JTextField(String.valueOf(currentItem.getPrepTime().toMinutes()));

            // Ingredient selection fields with prefilled data
            JComboBox<String> ingredientsComboBox1 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
            JTextField quantityField1 = new JTextField();
            JComboBox<String> ingredientsComboBox2 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
            JTextField quantityField2 = new JTextField();
            JComboBox<String> ingredientsComboBox3 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
            JTextField quantityField3 = new JTextField();
            JComboBox<String> ingredientsComboBox4 = new JComboBox<>(inventory.getAllIngredients().toArray(new String[0]));
            JTextField quantityField4 = new JTextField();

            // Prefill ingredients and quantities if available
            if (currentItem.getIngredients().size() > 0) {
                ingredientsComboBox1.setSelectedItem(currentItem.getIngredients().get(0).getName());
                quantityField1.setText(String.valueOf(currentItem.getIngredients().get(0).getQuantity()));
            }
            if (currentItem.getIngredients().size() > 1) {
                ingredientsComboBox2.setSelectedItem(currentItem.getIngredients().get(1).getName());
                quantityField2.setText(String.valueOf(currentItem.getIngredients().get(1).getQuantity()));
            }
            if (currentItem.getIngredients().size() > 2) {
                ingredientsComboBox3.setSelectedItem(currentItem.getIngredients().get(2).getName());
                quantityField3.setText(String.valueOf(currentItem.getIngredients().get(2).getQuantity()));
            }
            if (currentItem.getIngredients().size() > 3) {
                ingredientsComboBox4.setSelectedItem(currentItem.getIngredients().get(3).getName());
                quantityField4.setText(String.valueOf(currentItem.getIngredients().get(3).getQuantity()));
            }

            JPanel panel = new JPanel(new GridLayout(12, 4));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Description:"));
            panel.add(descriptionField);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Prep Time (in minutes):"));
            panel.add(prepTimeField);
            panel.add(new JLabel("Ingredient 1:"));
            panel.add(ingredientsComboBox1);
            panel.add(new JLabel("Quantity 1:"));
            panel.add(quantityField1);
            panel.add(new JLabel("Ingredient 2:"));
            panel.add(ingredientsComboBox2);
            panel.add(new JLabel("Quantity 2:"));
            panel.add(quantityField2);
            panel.add(new JLabel("Ingredient 3:"));
            panel.add(ingredientsComboBox3);
            panel.add(new JLabel("Quantity 3:"));
            panel.add(quantityField3);
            panel.add(new JLabel("Ingredient 4:"));
            panel.add(ingredientsComboBox4);
            panel.add(new JLabel("Quantity 4:"));
            panel.add(quantityField4);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Menu Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String description = descriptionField.getText();
                double price = Double.parseDouble(priceField.getText());
                Duration prepTime = Duration.ofMinutes(Long.parseLong(prepTimeField.getText()));

                // Prepare the updated ingredients list
                List<Ingredient> ingredients = new ArrayList<>();
                addIngredientToList(ingredients, ingredientsComboBox1, quantityField1);
                addIngredientToList(ingredients, ingredientsComboBox2, quantityField2);
                addIngredientToList(ingredients, ingredientsComboBox3, quantityField3);
                addIngredientToList(ingredients, ingredientsComboBox4, quantityField4);

                MenuItem updatedItem = new MenuItem(name, description, prepTime, price, new ArrayList<>(ingredients));
                if (menu.editMenuItem(selectedName, updatedItem)) {
                    JOptionPane.showMessageDialog(this, "Menu item updated successfully.");
                    updateMenuTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update menu item.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a menu item to edit.");
        }
    }


    private void handleDeleteMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0) {
            String selectedName = (String) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the menu item: " + selectedName + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (menu.removeMenuItem(selectedName)) {
                    JOptionPane.showMessageDialog(this, "Menu item deleted successfully.");
                    updateMenuTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete menu item.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a menu item to delete.");
        }
    }


    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        return String.format("%d min", minutes);
    }
}
