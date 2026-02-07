package coffeemachine.enums;

/**
 * Optional toppings that can be added to coffee
 */
public enum Topping {
    EXTRA_SUGAR("Extra Sugar", 10, Ingredient.SUGAR, 5),
    CARAMEL_SYRUP("Caramel Syrup", 20, Ingredient.CARAMEL_SYRUP, 10),
    WHIPPED_CREAM("Whipped Cream", 15, Ingredient.WHIPPED_CREAM, 20),
    EXTRA_SHOT("Extra Shot", 30, Ingredient.COFFEE_BEANS, 5);

    private final String displayName;
    private final int price;
    private final Ingredient ingredient;
    private final int quantity;

    Topping(String displayName, int price, Ingredient ingredient, int quantity) {
        this.displayName = displayName;
        this.price = price;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPrice() {
        return price;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }
}
