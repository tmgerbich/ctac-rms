package com.rms.service;

import com.rms.enums.OrderStatus;
import com.rms.model.Order;
import com.rms.model.Table;
import com.rms.util.FileManager;

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

    // Get a menu item by ID
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

    public Order getOrderForTable(Table table) {
        List<Order> activeOrders = getActiveOrders();
        for (Order order : activeOrders) {
            if (order.getTable().equals(table)) {
                return order;
            }
        }
        return null;
    }

    private void saveOrders() {
        FileManager.saveOrders(orders, "orders.dat");
    }

    private void loadOrders() {
        orders = FileManager.loadOrders("orders.dat");
        nextOrderId = orders.size() > 0 ? orders.keySet().stream().max(Integer::compare).orElse(0) + 1 : 1;
    }
}
