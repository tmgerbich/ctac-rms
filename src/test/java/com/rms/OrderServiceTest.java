package com.rms;

import com.rms.enums.OrderStatus;
import com.rms.model.Ingredient;
import com.rms.model.MenuItem;
import com.rms.model.Order;
import com.rms.model.Table;
import com.rms.service.Inventory;
import com.rms.service.OrderProcessor;
import com.rms.service.OrderService;
import com.rms.util.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderService orderService;
    private Inventory inventoryMock;
    private Order orderMock;
    private MenuItem menuItemMock;
    private Ingredient ingredientMock;
    private Table tableMock;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(true); // true for a new day
        inventoryMock = mock(Inventory.class);
        orderMock = mock(Order.class);
        menuItemMock = mock(MenuItem.class);
        ingredientMock = mock(Ingredient.class);
        tableMock = mock(Table.class);
    }

    @Test
    void addOrder() {
        when(orderMock.getItems()).thenReturn(new ArrayList<>());
        orderService.addOrder(orderMock);

        assertNotNull(orderService.getOrder(orderMock.getOrderID()));
    }


    @Test
    void updateOrderStatus_withNonExistentOrderId() {
        // Attempt to update the status of an order with a non-existent ID (e.g., 999)
        orderService.updateOrderStatus(999, OrderStatus.COMPLETED);

        // Since the order with ID 999 doesn't exist, getOrder should return null
        assertNull(orderService.getOrder(999));
    }


    @Test
    void getTotalRevenue() {
        when(orderMock.getPrice()).thenReturn(50.0);
        orderService.addOrder(orderMock);

        assertEquals(50.0, orderService.getTotalRevenue());
    }

    @Test
    void getMostPopularItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(menuItemMock);
        when(menuItemMock.getName()).thenReturn("Burger");
        when(orderMock.getItems()).thenReturn((ArrayList<MenuItem>) items);
        orderService.addOrder(orderMock);

        List<String> popularItems = orderService.getMostPopularItems();
        assertFalse(popularItems.isEmpty());
        assertTrue(popularItems.get(0).contains("Burger"));
    }

    @Test
    void getDetailedOrders() {
        orderService.addOrder(orderMock);

        List<Order> detailedOrders = orderService.getDetailedOrders();
        assertEquals(1, detailedOrders.size());
    }

    @Test
    void getTableSales() {
        when(orderMock.isTakeOut()).thenReturn(false);
        when(orderMock.getTable()).thenReturn(tableMock);
        when(tableMock.getTableName()).thenReturn("Table 1");
        when(orderMock.getPrice()).thenReturn(50.0);
        orderService.addOrder(orderMock);

        Map<String, Double> tableSales = orderService.getTableSales();
        assertEquals(50.0, tableSales.get("Table 1"));
    }

    @Test
    void getTakeoutSales() {
        when(orderMock.isTakeOut()).thenReturn(true);
        when(orderMock.getPrice()).thenReturn(30.0);
        orderService.addOrder(orderMock);

        assertEquals(30.0, orderService.getTakeoutSales());
    }

    @Test
    void getOrder() {
        orderService.addOrder(orderMock);
        Order foundOrder = orderService.getOrder(orderMock.getOrderID());

        assertNotNull(foundOrder);
        assertEquals(orderMock, foundOrder);
    }

    @Test
    void getAllOrders() {
        orderService.addOrder(orderMock);

        List<Order> allOrders = orderService.getAllOrders();
        assertEquals(1, allOrders.size());
    }

    @Test
    void getActiveOrders() {
        when(orderMock.getStatus()).thenReturn(OrderStatus.ACTIVE);
        orderService.addOrder(orderMock);

        List<Order> activeOrders = orderService.getActiveOrders();
        assertEquals(1, activeOrders.size());
    }

    @Test
    void getActiveTableOrders() {
        when(orderMock.getStatus()).thenReturn(OrderStatus.ACTIVE);
        when(orderMock.isTakeOut()).thenReturn(false);
        orderService.addOrder(orderMock);

        List<Order> activeTableOrders = orderService.getActiveTableOrders();
        assertEquals(1, activeTableOrders.size());
    }

    @Test
    void getOrderForTable() {
        when(orderMock.getTable()).thenReturn(tableMock);
        orderService.addOrder(orderMock);

        Order foundOrder = orderService.getOrderForTable(tableMock);
        assertEquals(orderMock, foundOrder);
    }

    @Test
    void subtractIngredients_successful() {
        ArrayList<MenuItem> items = new ArrayList<>();
        items.add(menuItemMock);
        when(orderMock.getItems()).thenReturn(items);

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientMock);
        when(menuItemMock.getIngredients()).thenReturn(ingredients);
        when(ingredientMock.getName()).thenReturn("Tomato");
        when(ingredientMock.getQuantity()).thenReturn((int) 1.0);
        when(inventoryMock.subtractIngredient("Tomato", (int) 1.0)).thenReturn(true);

        boolean result = orderService.subtractIngredients(inventoryMock, orderMock);
        assertTrue(result);
    }

    @Test
    void subtractIngredients_unsuccessful() {
        ArrayList<MenuItem> items = new ArrayList<>();
        items.add(menuItemMock);
        when(orderMock.getItems()).thenReturn(items);

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientMock);
        when(menuItemMock.getIngredients()).thenReturn(ingredients);
        when(ingredientMock.getName()).thenReturn("Tomato");
        when(ingredientMock.getQuantity()).thenReturn((int) 1.0);
        when(inventoryMock.subtractIngredient("Tomato", (int) 1.0)).thenReturn(false);

        boolean result = orderService.subtractIngredients(inventoryMock, orderMock);
        assertFalse(result);
    }
}
