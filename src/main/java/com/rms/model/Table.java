package com.rms.model;

import com.rms.enums.TableStatus;
import com.rms.gui.TableSetup;

import java.io.Serializable;

public class Table implements Serializable {
    private TableSetup tableSetup;
    private TableStatus tableStatus;
    private String customerName;
    private Order order;

    public Table(TableSetup setup) {
        this.tableSetup = setup;
        this.tableStatus = TableStatus.AVAILABLE;
    }

    public String getTableName() {
        return tableSetup.getTableName();
    }

    public int getSeats() {
        return tableSetup.getSeats();
    }

    public TableStatus getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(TableStatus status) {
        this.tableStatus = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
