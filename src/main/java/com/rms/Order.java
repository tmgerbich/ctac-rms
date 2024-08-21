package com.rms;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;

public class Order implements Serializable {
    private int orderID;
    private boolean takeOut;
    private Table table;
    private String name;
    private ArrayList<MenuItem> items;
    private double price;
    private OrderStatus status;
    private Duration prepTime;

    public Order(boolean takeOut, String name, ArrayList<MenuItem> items) {
        this.takeOut = takeOut;
        this.items = items;
        this.table = null;
        this.name = name;
        calculatePrice();
        calculatePrepTime();
        this.status = OrderStatus.WAITING;
    }

    public Order(Table table, ArrayList<MenuItem> items) {
        this.table = table;
        this.items = items;
        this.takeOut = false;
        calculatePrice();
        calculatePrepTime();
        this.status = OrderStatus.WAITING;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public boolean isTakeOut() {
        return takeOut;
    }

    public double getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    //does this take into account quantity??
    private void calculatePrice() {
        this.price = items.stream().mapToDouble(MenuItem::getPrice).sum();
    }

    private void calculatePrepTime() {
        this.prepTime = items.stream()
                .map(MenuItem::getPrepTime)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public Duration getPrepTime() {
        return prepTime;
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
