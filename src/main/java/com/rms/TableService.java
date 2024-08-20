package com.rms;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableService {
    private Map<String, Table> tables;
    private boolean newDay;

    public TableService(boolean newDay) {
        this.tables = new HashMap<>();
        this.newDay = newDay;
        if (!newDay) {
            File file = new File("tables.dat");
            // Check if the file exists
            if (file.exists()) {
                loadTables();
            }
        }
        if (newDay) {
            for(TableSetup tableSetup : TableSetup.values()){
                tables.put(tableSetup.name(), new Table(tableSetup));
            }

        }
        saveTables();
    }

    public Map<String, Table> getTables() {
        return tables;
    }

    public void setTables(Map<String, Table> tables) {
        this.tables = tables;
        saveTables();
    }

    // Getter for individual Table by table name
    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    // Setter for individual Table by table name
    public void setTable(String tableName, Table table) {
        tables.put(tableName, table);
        saveTables();
    }

    private void saveTables() {
        FileManager.saveTables(tables, "tables.dat");
    }

    private void loadTables() {
        tables = FileManager.loadTables("tables.dat");
    }

    public List<String> getAllTables() {
        return tables.keySet().stream().collect(Collectors.toList());
    }
}
