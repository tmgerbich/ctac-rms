package com.rms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Menu {
    private List<MenuItem> menuItems;

    public Menu() {
        this.menuItems = new ArrayList<>();
        File file = new File("menuItems.dat");
        if (file.exists()) {
            loadMenuItems(); // Load menu items from the file if it exists
        }
    }

    // Add a new item to the menu
    public boolean addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        saveMenuItems();
        return true;
    }

    // Remove an item from the menu by name
    public boolean removeMenuItem(String name) {
        MenuItem itemToRemove = null;
        for (MenuItem item : menuItems) {
            if (item.getName().equals(name)) {
                itemToRemove = item;
                break;
            }
        }
        if (itemToRemove != null) {
            menuItems.remove(itemToRemove);
            saveMenuItems();
            return true;
        } else {
            System.out.println("Menu item not found.");
            return false;
        }
    }

    // Edit an existing menu item
    public boolean editMenuItem(String name, MenuItem updatedMenuItem) {
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getName().equals(name)) {
                menuItems.set(i, updatedMenuItem);
                saveMenuItems();
                return true;
            }
        }
        System.out.println("Menu item not found.");
        return false;
    }

    // Get a menu item by name
    public MenuItem getMenuItem(String name) {
        for (MenuItem item : menuItems) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    // Display the entire menu
    @Override
    public String toString() {
        StringBuilder menuList = new StringBuilder("Menu:\n");
        for (MenuItem item : menuItems) {
            menuList.append(item).append("\n");
        }
        return menuList.toString();
    }

    private void saveMenuItems() {
        FileManager.saveMenuItems(menuItems, "menuItems.dat");
    }

    private void loadMenuItems() {
        menuItems = FileManager.loadMenuItems("menuItems.dat");
    }

    // Get all menu item names
    public List<String> getAllMenuItems() {
        return menuItems.stream().map(MenuItem::getName).collect(Collectors.toList());
    }
}
