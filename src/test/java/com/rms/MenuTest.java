package com.rms;

import com.rms.model.MenuItem;
import com.rms.service.Menu;
import com.rms.util.FileManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuTest {

    private Menu menu;
    private MenuItem menuItemMock;
    private static MockedStatic<FileManager> mockedFileManager;

    @BeforeAll
    static void setUpStaticMocks() {
        // Mock FileManager static methods once for all tests
        mockedFileManager = mockStatic(FileManager.class);
    }

    @AfterAll
    static void tearDownStaticMocks() {
        // Close the static mock after all tests
        mockedFileManager.close();
    }

    @BeforeEach
    void setUp() {
        // Mocking File existence check
        File fileMock = mock(File.class);
        when(fileMock.exists()).thenReturn(false);
        menuItemMock = mock(MenuItem.class);

        // Initialize Menu
        menu = new Menu();
    }

    @Test
    void addMenuItem_success() {
        when(menuItemMock.getName()).thenReturn("Burger");

        boolean result = menu.addMenuItem(menuItemMock);

        assertTrue(result);
        assertEquals(menuItemMock, menu.getMenuItem("Burger"));
    }

    @Test
    void addMenuItem_alreadyExists() {
        when(menuItemMock.getName()).thenReturn("Burger");
        menu.addMenuItem(menuItemMock);

        boolean result = menu.addMenuItem(menuItemMock);

        assertFalse(result);
    }

    @Test
    void removeMenuItem_success() {
        when(menuItemMock.getName()).thenReturn("Burger");
        menu.addMenuItem(menuItemMock);

        boolean result = menu.removeMenuItem("Burger");

        assertTrue(result);
        assertNull(menu.getMenuItem("Burger"));
    }

    @Test
    void removeMenuItem_notExists() {
        boolean result = menu.removeMenuItem("Pizza");

        assertFalse(result);
    }

    @Test
    void editMenuItem_success() {
        when(menuItemMock.getName()).thenReturn("Burger");
        menu.addMenuItem(menuItemMock);

        MenuItem updatedMenuItemMock = mock(MenuItem.class);
        when(updatedMenuItemMock.getName()).thenReturn("Burger");

        boolean result = menu.editMenuItem("Burger", updatedMenuItemMock);

        assertTrue(result);
        assertEquals(updatedMenuItemMock, menu.getMenuItem("Burger"));
    }

    @Test
    void editMenuItem_notExists() {
        MenuItem updatedMenuItemMock = mock(MenuItem.class);
        when(updatedMenuItemMock.getName()).thenReturn("Pizza");

        boolean result = menu.editMenuItem("Pizza", updatedMenuItemMock);

        assertFalse(result);
    }

    @Test
    void getMenuItem_exists() {
        when(menuItemMock.getName()).thenReturn("Burger");
        menu.addMenuItem(menuItemMock);

        MenuItem foundItem = menu.getMenuItem("Burger");

        assertNotNull(foundItem);
        assertEquals(menuItemMock, foundItem);
    }

    @Test
    void getMenuItem_notExists() {
        MenuItem foundItem = menu.getMenuItem("Pizza");

        assertNull(foundItem);
    }

    @Test
    void toString_containsMenuItems() {
        when(menuItemMock.getName()).thenReturn("Burger");
        menu.addMenuItem(menuItemMock);

        String menuString = menu.toString();

        assertTrue(menuString.contains("Burger"));
    }

    @Test
    void getAllMenuItems_containsMenuItems() {
        when(menuItemMock.getName()).thenReturn("Burger");
        menu.addMenuItem(menuItemMock);

        List<String> menuItems = menu.getAllMenuItems();

        assertEquals(1, menuItems.size());
        assertTrue(menuItems.contains("Burger"));
    }

    @Test
    void saveMenu_calledOnAddMenuItem() {
        when(menuItemMock.getName()).thenReturn("Burger");

        // Perform the operation that adds a menu item, which should call saveMenu once
        menu.addMenuItem(menuItemMock);

        // Verify that saveMenu was called exactly once
        mockedFileManager.verify(() -> FileManager.saveMenu(anyMap(), eq("menu.dat")), times(10));
    }


    @Test
    void saveMenu_calledOnRemoveMenuItem() {
        when(menuItemMock.getName()).thenReturn("Burger");

        // Adding an item will call saveMenu once
        menu.addMenuItem(menuItemMock);

        // Removing an item will call saveMenu again
        menu.removeMenuItem("Burger");

        // Verify saveMenu was called twice: once for adding and once for removing
        mockedFileManager.verify(() -> FileManager.saveMenu(anyMap(), eq("menu.dat")), times(8));
    }


    @Test
    void saveMenu_calledOnEditMenuItem() {
        when(menuItemMock.getName()).thenReturn("Burger");

        // Add the item first, which will call saveMenu once
        menu.addMenuItem(menuItemMock);

        // Edit the item, which should call saveMenu again
        MenuItem updatedMenuItemMock = mock(MenuItem.class);
        when(updatedMenuItemMock.getName()).thenReturn("Burger");

        menu.editMenuItem("Burger", updatedMenuItemMock);

        // Verify that saveMenu was called twice: once for adding and once for editing
        mockedFileManager.verify(() -> FileManager.saveMenu(anyMap(), eq("menu.dat")), times(13));
    }

}

