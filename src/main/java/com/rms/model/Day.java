package com.rms.model;

import com.rms.enums.DayNewOrNot;
import com.rms.util.FileManager;

import java.io.File;

public class Day {
    private DayNewOrNot newOrNot;

    public Day() {
        this.newOrNot = newOrNot.OLD;
        File file = new File("dayNewOrNot.dat");
        // Check if the file exists
        if (file.exists()) {
            loadDayNewOrNot(); // Load day from the file if it exists
        }
    }

    public void setNewOrNot(DayNewOrNot newOrNot) {
        this.newOrNot = newOrNot;
    }

    public DayNewOrNot getNewOrNot() {
        return newOrNot;
    }

    public void saveDayNewOrNot() {
        FileManager.saveDayNewOrNot(newOrNot, "dayNewOrNot.dat");
    }

    public void loadDayNewOrNot() {
        newOrNot = FileManager.loadDayNewOrNot("dayNewOrNot.dat");}
}
