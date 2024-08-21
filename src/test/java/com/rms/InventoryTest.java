package com.rms;

import com.rms.model.Ingredient;
import com.rms.service.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();  // Create a new Inventory instance before each test
    }

    @Test
    void testAddIngredient() {
        Ingredient ingredient = new Ingredient("Tomato", 10, "pcs");

        boolean result = inventory.addIngredient(ingredient);

        assertTrue(result, "Ingredient should be added successfully.");
        assertEquals(1, inventory.getAllIngredients().size(), "Inventory should contain 1 ingredient.");
        assertEquals(ingredient, inventory.getIngredient("Tomato"), "The added ingredient should be retrievable by name.");
    }

    @Test
    void testRemoveIngredient() {
        Ingredient ingredient = new Ingredient("Tomato", 10, "pcs");
        inventory.addIngredient(ingredient);

        boolean result = inventory.removeIngredient("Tomato", 5);

        assertTrue(result, "Ingredient should be removed successfully.");
        assertEquals(5, inventory.getIngredient("Tomato").getQuantity(), "The quantity of the ingredient should be updated correctly.");

        // Remove the remaining quantity
        result = inventory.removeIngredient("Tomato", 5);
        assertTrue(result, "Remaining ingredient should be removed successfully.");
        assertNull(inventory.getIngredient("Tomato"), "Ingredient should no longer exist in inventory.");
    }
}

