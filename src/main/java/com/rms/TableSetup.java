package com.rms;

public enum TableSetup {
    TABLE_ONE("1", 4),
    TABLE_TWO("2", 4),
    TABLE_THREE("3", 4),
    TABLE_FOUR("4", 4),
    TABLE_FIVE("5", 4),
    TABLE_SIX("6", 6);

    private final String tableName;
    private final int seats;

    TableSetup(String tableName, int seats) {
        this.tableName = tableName;
        this.seats = seats;
    }

    public String getTableName() {
        return tableName;
    }

    public int getSeats() {
        return seats;
    }
}
