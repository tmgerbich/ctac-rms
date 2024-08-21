package com.rms;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Inventory {
    private Map<String, Ingredient> ingredients;


    public Inventory() {
        this.ingredients = new HashMap<>();
        File file = new File("ingredients.dat");
        // Check if the file exists
        if (file.exists()) {
            loadIngredients(); // Load ingredients from the file if it exists
        }

    }


    public boolean addIngredient(Ingredient ingredient) {
        String name = ingredient.getName();
        if (ingredients.containsKey(name)) {
            // If ingredient already exists, update its quantity
            Ingredient existing = ingredients.get(name);
            existing.setQuantity(existing.getQuantity() + ingredient.getQuantity());
        } else {
            // Otherwise, add it as a new ingredient
            ingredients.put(name, ingredient);
        }
        saveIngredients();
        return true;
    }


    public boolean subtractIngredient(String name, int quantity) {
        if (ingredients.containsKey(name)) {
            Ingredient ingredient = ingredients.get(name);
            int currentQuantity = ingredient.getQuantity();
            if (currentQuantity >= quantity) {
                ingredient.setQuantity(currentQuantity - quantity);
                saveIngredients();
                return true;
            } else {
                System.out.println("Not enough " + name + " in inventory to remove.");
                return false;
            }
        } else {
            System.out.println(name + " not found in inventory.");
            return false;
        }
    }

    // Get an ingredient from the inventory
    public Ingredient getIngredient(String name) {
        return ingredients.get(name);
    }

    // Display the entire inventory
    @Override
    public String toString() {
        StringBuilder inventoryList = new StringBuilder("Inventory:\n");
        for (Ingredient ingredient : ingredients.values()) {
            inventoryList.append(ingredient).append("\n");
        }
        return inventoryList.toString();
    }

    private void saveIngredients() {
        FileManager.saveIngredients(ingredients, "ingredients.dat");
    }

    private void loadIngredients() {
        ingredients = FileManager.loadIngredients("ingredients.dat");
    }

    public List<String> getAllIngredients() {
        return ingredients.keySet().stream().collect(Collectors.toList());
    }

}
