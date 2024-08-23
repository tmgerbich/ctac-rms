package com.rms.service;

import com.rms.enums.OrderStatus;
import com.rms.model.Ingredient;
import com.rms.model.MenuItem;
import com.rms.model.Order;
import com.rms.model.Table;
import com.rms.util.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderService {
    private Map<Integer, Order> orders;
    private int nextOrderId;
    private OrderProcessor orderProcessor;

    public OrderService(boolean newDay) {
        this.orders = new HashMap<>();
        this.orderProcessor = new OrderProcessor(4); // 4 threads
        if (!newDay) {
            loadOrders();
        } else {
            nextOrderId = 1;
        }
    }

    public void addOrder(Order order) {
        order.setOrderID(nextOrderId++);
        orders.put(order.getOrderID(), order);
        orderProcessor.submitOrder(order); // Submit the order for processing
        saveOrders();
    }

    // Shutdown the processor when the application stops
    public void shutdownProcessor() {
        orderProcessor.shutdown();
    }

    public void updateOrderStatus(int orderId, OrderStatus status) {
        if (orders.containsKey(orderId)) {
            orders.get(orderId).setStatus(status);
            saveOrders();
        }
    }

    public double getTotalRevenue() {
        return orders.values().stream()
                .mapToDouble(Order::getPrice)
                .sum();
    }

    public List<String> getMostPopularItems() {
        // Create a map to count the occurrences of each item
        Map<String, Long> itemCount = orders.values().stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(MenuItem::getName, Collectors.counting()));

        // Sort the map by value in descending order and return the top items
        return itemCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " orders")
                .collect(Collectors.toList());
    }

    public List<Order> getDetailedOrders() {
        return orders.values().stream()
                .collect(Collectors.toList());
    }

    public Map<String, Double> getTableSales() {
        return orders.values().stream()
                .filter(order -> !order.isTakeOut())
                .collect(Collectors.groupingBy(
                        order -> order.getTable().getTableName(), // Group by table name or ID
                        Collectors.summingDouble(Order::getPrice) // Sum the total prices for each table
                ));
    }



    // Get an order by ID
    public Order getOrder(int orderId) {
        for (Map.Entry<Integer, Order> entry : orders.entrySet()) {
            if (entry.getKey().equals(orderId)) {
                return orders.get(orderId);
            }
        }
        return null;
    }

    public List<Order> getAllOrders() {
        return orders.values().stream().collect(Collectors.toList());
    }

    public List<Order> getActiveOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() != OrderStatus.CLEARED)
                .collect(Collectors.toList());
    }

    public List<Order> getActiveTableOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() != OrderStatus.CLEARED)
                .filter(order -> !order.isTakeOut())
                .collect(Collectors.toList());
    }



    public Order getOrderForTable(Table table) {
        List<Order> activeOrders = getActiveTableOrders();
        for (Order order : activeOrders) {
            if (order.getTable().equals(table)) {
                return order;
            }
        }
        return null;
    }

    public void saveOrders() {
        FileManager.saveOrders(orders, "orders.dat");
    }

    private void loadOrders() {
        orders = FileManager.loadOrders("orders.dat");
        nextOrderId = orders.size() > 0 ? orders.keySet().stream().max(Integer::compare).orElse(0) + 1 : 1;
    }

    public boolean subtractIngredients(Inventory inventory, Order order) {
        ArrayList<MenuItem> orders = order.getItems();
        List<Ingredient> successfullySubtracted = new ArrayList<>();

        for (MenuItem item : orders) {
            List<Ingredient> ingredients = item.getIngredients();
            for (Ingredient ingredient : ingredients) {
                boolean success = inventory.subtractIngredient(ingredient.getName(), ingredient.getQuantity());
                if (success) {
                    // Keep track of successfully subtracted ingredients for potential rollback
                    successfullySubtracted.add(new Ingredient(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit()));
                } else {
                    // If subtraction fails, revert the previously subtracted ingredients
                    for (Ingredient subtractedIngredient : successfullySubtracted) {
                        inventory.addIngredient(subtractedIngredient);
                    }
                    return false; // Return false as the subtraction failed
                }
            }
        }

        return true; // Return true if all ingredients were successfully subtracted
    }
}
