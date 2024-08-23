package com.rms;

import com.rms.model.Ingredient;
import com.rms.service.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InventoryTest {
    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        inventory = new Inventory();
    }

    @Test
    public void testAddIngredient() {
        Ingredient ingredient = new Ingredient("Tomato", 50, "kg");
        inventory.addIngredient(ingredient);

        Ingredient result = inventory.getIngredient("Tomato");
        assertNotNull(result); // Ensure the ingredient was added
        assertEquals("Tomato", result.getName());
        assertEquals(50, result.getQuantity());
        assertEquals("kg", result.getUnit());
    }


    @Test
    public void testRemoveIngredient() {
        Ingredient ingredient = new Ingredient("Tomato", 50, "kg");
        inventory.addIngredient(ingredient);
        inventory.removeIngredient("Tomato");

        Ingredient result = inventory.getIngredient("Tomato");
        assertEquals(null, result); // Ensure the ingredient was removed
    }
}
