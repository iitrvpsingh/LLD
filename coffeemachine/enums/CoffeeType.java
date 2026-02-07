package coffeemachine.enums;

/**
 * Types of coffee available
 */
public enum CoffeeType {
    ESPRESSO("Espresso", 150),
    LATTE("Latte", 220),
    CAPPUCCINO("Cappuccino", 200),
    AMERICANO("Americano", 180),
    MOCHA("Mocha", 250);

    private final String displayName;
    private final int basePrice;

    CoffeeType(String displayName, int basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBasePrice() {
        return basePrice;
    }
}
