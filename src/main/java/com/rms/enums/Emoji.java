package com.rms.enums;

public enum Emoji {
    // Fruits
    APPLE("\uD83C\uDF4E"), // 🍎

    // Vegetables
    CORN("\uD83C\uDF3D"), // 🌽
    TOMATO("\uD83C\uDF45"), // 🍅
    LETTUCE("\uD83E\uDDC7"), // 🥬

    // Dairy & Protein
    CHEESE("\uD83E\uDDC0"), // 🧀
    POULTRY_LEG("\uD83C\uDF57"), // 🍗
    HAMBURGER("\uD83C\uDF54"), // 🍔
    HOT_DOG("\uD83C\uDF2D"), // 🌭
    SANDWICH("\uD83E\uDD6A"), // 🥪
    PIZZA("\uD83C\uDF55"), // 🍕

    // Snacks & Desserts
    PRETZEL("\uD83E\uDD68"), // 🥨
    FRENCH_FRIES("\uD83C\uDF5F"), // 🍟
    COOKIE("\uD83C\uDF6A"), // 🍪
    LOLLIPOP("\uD83C\uDF6D"), // 🍭
    ICE_CREAM("\uD83C\uDF66"), // 🍦

    // Beverages
    SODA("\uD83C\uDF79"), // 🍹 (Using a cocktail glass for soda)
    WATER_BOTTLE("\uD83E\uDD64"), // 🥤 (Cup with straw as a close match)
    JUICE_BOX("\uD83C\uDF7A"), // 🧃 (Using the tumbler glass as a juice box alternative)

    ;

    private final String symbol;

    Emoji(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
