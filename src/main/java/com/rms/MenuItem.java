package com.rms;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

public class MenuItem implements Serializable {
    private String name;
    private String description;
    private double price;
    private String category;
    private Duration preparationTime;  // using Duration instead of int
    private List<Ingredient> ingredients;

    public MenuItem(String name, String description, double price, String category, Duration preparationTime, List<Ingredient> ingredients) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.preparationTime = preparationTime;
        this.ingredients = ingredients;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Duration getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Duration preparationTime) {
        this.preparationTime = preparationTime;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f: %s [Prep Time: %s, Ingredients: %s]",
                name, category, price, description, formatDuration(preparationTime), ingredients);
    }

    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        return String.format("%d min, %d sec", minutes, seconds);
    }
}

