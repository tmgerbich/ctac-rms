package com.rms.service;

import com.rms.model.MenuItem;
import com.rms.util.FileManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Menu {
    private Map<String, MenuItem> menu;


    public Menu() {
        this.menu = new HashMap<>();
        File file = new File("menu.dat");
        // Check if the file exists
        if (file.exists()) {
            loadMenu(); // Load ingredients from the file if it exists
        }

    }

    public boolean addMenuItem(MenuItem menuItem) {
        String name = menuItem.getName();
        if (menu.containsKey(name)) {
            System.out.println(name + "already on the menu.");
            // If menu item already exists, do nothing
            return false;
        } else {
            // Otherwise, add it as a new item
            menu.put(name, menuItem);
        }
        saveMenu();
        return true;
    }

    // Remove an item from the menu
    public boolean removeMenuItem(String name) {
        if (menu.containsKey(name)) {
            MenuItem menuItem = menu.get(name);
                menu.remove(name);
                saveMenu();
                return true;
            }
         else {
            System.out.println(name + "not on the menu to remove.");
            return false;
        }
    }

    // Edit an existing menu item
    public boolean editMenuItem(String name, MenuItem updatedMenuItem) {
            if (menu.containsKey(name)) {
                menu.put(name, updatedMenuItem);
                saveMenu();
                return true;
            }
        System.out.println("Menu item not found.");
        return false;
    }

    // Get a menu item by name
    public MenuItem getMenuItem(String name) {
        for (Map.Entry<String, MenuItem> entry : menu.entrySet()) {
            if (entry.getKey().equals(name)) {
                return menu.get(name);
            }
        }
        return null;
    }

    // Display the entire menu
    @Override
    public String toString() {
        StringBuilder menuList = new StringBuilder("Menu:\n");
        for (Map.Entry<String, MenuItem> entry : menu.entrySet()) {
            menuList.append(entry.getKey()).append("\n");
        }
        return menuList.toString();
    }


    private void saveMenu() {
        FileManager.saveMenu(menu, "menu.dat");
    }

    private void loadMenu() {
        menu = FileManager.loadMenu("menu.dat");
    }

    public List<String> getAllMenuItems() {
        return menu.keySet().stream().collect(Collectors.toList());
    }
}
