package com.rms;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderService {
    private Map<Integer, Order> orders;
    private int nextOrderId;

    public OrderService(boolean newDay) {
        this.orders = new HashMap<>();
        if (!newDay) {
            File file = new File("tables.dat");
            // Check if the file exists
            if (file.exists()) {
                loadOrders();
            }
        }
             else {
            nextOrderId = 1;
        }
    }

    public void addOrder(Order order) {
        order.setOrderID(nextOrderId++);
        orders.put(order.getOrderID(), order);
        saveOrders();
    }

    public void updateOrderStatus(int orderId, OrderStatus status) {
        if (orders.containsKey(orderId)) {
            orders.get(orderId).setStatus(status);
            saveOrders();
        }
    }

    public List<Order> getAllOrders() {
        return orders.values().stream().collect(Collectors.toList());
    }

    public List<Order> getActiveOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() != OrderStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    private void saveOrders() {
        FileManager.saveOrders(orders, "orders.dat");
    }

    private void loadOrders() {
        orders = FileManager.loadOrders("orders.dat");
        nextOrderId = orders.size() > 0 ? orders.keySet().stream().max(Integer::compare).orElse(0) + 1 : 1;
    }
}
