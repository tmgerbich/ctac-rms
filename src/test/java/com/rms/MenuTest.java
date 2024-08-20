package com.rms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();  // Create a new Menu instance before each test
    }

    @Test
    void testAddMenuItem() {
        MenuItem burger = new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);

        boolean result = menu.addMenuItem(burger);

        assertTrue(result, "Menu item should be added successfully.");
        assertEquals(1, menu.getAllMenuItems().size(), "Menu should contain 1 item.");
        assertEquals(burger, menu.getMenuItem("Burger"), "The added item should be retrievable by name.");
    }

    @Test
    void testAddDuplicateMenuItem() {
        MenuItem burger = new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);
        menu.addMenuItem(burger);

        // Attempt to add the same item again
        boolean result = menu.addMenuItem(burger);

        assertFalse(result, "Duplicate menu item should not be added.");
        assertEquals(1, menu.getAllMenuItems().size(), "Menu should still contain only 1 item.");
    }

    @Test
    void testRemoveMenuItem() {
        MenuItem burger = new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);
        menu.addMenuItem(burger);

        boolean result = menu.removeMenuItem("Burger");

        assertTrue(result, "Menu item should be removed successfully.");
        assertEquals(0, menu.getAllMenuItems().size(), "Menu should be empty after removing the item.");
        assertNull(menu.getMenuItem("Burger"), "The removed item should not be retrievable.");
    }

    @Test
    void testRemoveNonExistentMenuItem() {
        boolean result = menu.removeMenuItem("Pizza");

        assertFalse(result, "Removing a non-existent item should return false.");
        assertEquals(0, menu.getAllMenuItems().size(), "Menu should still be empty.");
    }

    @Test
    void testEditMenuItem() {
        MenuItem burger = new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);
        menu.addMenuItem(burger);

        // Edit the menu item
        MenuItem updatedBurger = new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);
        boolean result = menu.editMenuItem("Burger", updatedBurger);

        assertTrue(result, "Menu item should be edited successfully.");
        assertEquals(9.99, menu.getMenuItem("Burger").getPrice(), "The item's price should be updated.");
        assertEquals("Delicious beef burger with cheese", menu.getMenuItem("Burger").getDescription(), "The item's description should be updated.");
    }

    @Test
    void testEditNonExistentMenuItem() {
        MenuItem updatedBurger = new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);
        boolean result = menu.editMenuItem("Burger", updatedBurger);

        assertFalse(result, "Editing a non-existent item should return false.");
    }

    @Test
    void testLoadMenuFromFile() {
        MenuItem burger =new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);
        menu.addMenuItem(burger);

        // Create a new menu instance to test loading
        Menu newMenu = new Menu();
        assertEquals(1, newMenu.getAllMenuItems().size(), "Loaded menu should contain 1 item.");
        assertEquals(burger, newMenu.getMenuItem("Burger"), "The loaded item should match the saved item.");
    }

    @Test
    void testToString() {
        MenuItem burger = new MenuItem("Burger", "Delicious beef burger", Duration.ofMinutes(15), 8.99, null);
        menu.addMenuItem(burger);

        String expectedOutput = "Menu:\nBurger\n";
        assertEquals(expectedOutput, menu.toString(), "Menu string representation should match the expected output.");
    }
}
