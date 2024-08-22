package com.rms.model;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MenuItem implements Serializable {
    private String name;
    private String description;
    private Duration prepTime;
    private double price;
    private ArrayList<Ingredient> ingredients;

    //fix this later
    public MenuItem(String name, String description, Duration prepTime, double price, ArrayList<Ingredient> ingredients) {
        this.name = name;
        this.description = description;
        this.prepTime = prepTime;
        this.price = price;
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


    public Duration getPrepTime() {
        return prepTime;
    }

    public void setPreparationTime(Duration prepTime) {
        this.prepTime = prepTime;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f: %s [Prep Time: %s, Ingredients: %s]",
                name, price, description, formatDuration(prepTime), ingredients);
    }

//    private String formatDuration(Duration duration) {
//        long minutes = duration.toMinutes();
//        long seconds = duration.minusMinutes(minutes).getSeconds();
//        return String.format("%d min, %d sec", minutes, seconds);

    // Format the duration for display
    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        return String.format("%d sec", seconds);
    }

    // Format the ingredients for display
    private String formatIngredients() {
        StringBuilder ingredientsList = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getQuantity() > 0) {
                ingredientsList.append(ingredient.getName()).append(" (").append(ingredient.getQuantity()).append("), ");
            }
        }
        if (ingredientsList.length() > 0) {
            ingredientsList.setLength(ingredientsList.length() - 2);  // Remove trailing comma and space
        }
        return ingredientsList.toString();
    }
}
