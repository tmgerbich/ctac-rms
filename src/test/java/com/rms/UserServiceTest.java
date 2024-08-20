package com.rms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Before each test, create a new UserService instance
        // and delete the "users.dat" file to ensure a clean state
        File file = new File("users.dat");
        if (file.exists()) {
            file.delete();
        }
        userService = new UserService();
    }

    @Test
    void testInitializeAdmin() {
        // The admin should be created automatically when the service is initialized
        User admin = userService.getUser("admin");

        assertNotNull(admin, "Admin user should be created upon initialization.");
        assertTrue(userService.authenticate("admin", "admin") instanceof Admin, "Admin user should be able to authenticate with the default password.");
    }

    @Test
    void testAddManagerByAdmin() {
        User admin = userService.getUser("admin");

        boolean result = userService.addUser("manager1", "password", "Manager", admin);

        assertTrue(result, "Admin should be able to add a manager.");
        assertTrue(userService.authenticate("manager1", "password") instanceof Manager, "The added manager should be able to authenticate.");
    }

    @Test
    void testAddStaffByManager() {
        User admin = userService.getUser("admin");
        userService.addUser("manager1", "password", "Manager", admin);
        User manager = userService.authenticate("manager1", "password");

        boolean result = userService.addUser("staff1", "password", "Staff", manager);

        assertTrue(result, "Manager should be able to add staff.");
        assertTrue(userService.authenticate("staff1", "password") instanceof Staff, "The added staff should be able to authenticate.");
    }

    @Test
    void testAddManagerByNonAdmin() {
        User admin = userService.getUser("admin");
        userService.addUser("manager1", "password", "Manager", admin);
        User manager = userService.authenticate("manager1", "password");

        boolean result = userService.addUser("manager2", "password", "Manager", manager);

        assertFalse(result, "Manager should not be able to add another manager.");
    }

    @Test
    void testRemoveManagerByAdmin() {
        User admin = userService.getUser("admin");
        userService.addUser("manager1", "password", "Manager", admin);

        boolean result = userService.removeUser("manager1", admin);

        assertTrue(result, "Admin should be able to remove a manager.");
        assertNull(userService.getUser("manager1"), "Removed manager should not exist in the system.");
    }

    @Test
    void testRemoveStaffByManager() {
        User admin = userService.getUser("admin");
        userService.addUser("manager1", "password", "Manager", admin);
        User manager = userService.authenticate("manager1", "password");
        userService.addUser("staff1", "password", "Staff", manager);

        boolean result = userService.removeUser("staff1", manager);

        assertTrue(result, "Manager should be able to remove staff.");
        assertNull(userService.getUser("staff1"), "Removed staff should not exist in the system.");
    }

    @Test
    void testAuthenticationSuccess() {
        User admin = userService.getUser("admin");

        User result = userService.authenticate("admin", "admin");

        assertNotNull(result, "Admin should be able to authenticate with the correct password.");
        assertEquals(admin, result, "Authenticated user should be the admin.");
    }

    @Test
    void testAuthenticationFailure() {
        User result = userService.authenticate("admin", "wrongpassword");

        assertNull(result, "Authentication should fail with an incorrect password.");
    }

    @Test
    void testSaveAndLoadUsers() {
        User admin = userService.getUser("admin");
        userService.addUser("manager1", "password", "Manager", admin);

        // Create a new UserService instance to test loading from file
        UserService newUserService = new UserService();
        User loadedManager = newUserService.authenticate("manager1", "password");

        assertNotNull(loadedManager, "Manager should be loaded from the file.");
        assertTrue(loadedManager instanceof Manager, "Loaded user should be a Manager.");
    }

    @Test
    void testGetAllUsernames() {
        User admin = userService.getUser("admin");
        userService.addUser("manager1", "password", "Manager", admin);
        userService.addUser("staff1", "password", "Staff", admin);

        List<String> usernames = userService.getAllUsernames();

        assertEquals(3, usernames.size(), "There should be 3 users in total (admin, manager1, staff1).");
        assertTrue(usernames.contains("admin"), "Usernames list should include 'admin'.");
        assertTrue(usernames.contains("manager1"), "Usernames list should include 'manager1'.");
        assertTrue(usernames.contains("staff1"), "Usernames list should include 'staff1'.");
    }
}
