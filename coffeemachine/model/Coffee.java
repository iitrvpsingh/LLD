package coffeemachine.model;

import coffeemachine.enums.CoffeeType;
import coffeemachine.enums.Ingredient;
import coffeemachine.enums.Topping;

import java.util.*;

/**
 * Represents a coffee order with base type and optional toppings
 * Simple, clean design - no complex patterns
 */
public class Coffee {
    private final CoffeeType type;
    private final List<Topping> toppings;
    private final Map<Ingredient, Integer> recipe;

    public Coffee(CoffeeType type, List<Topping> toppings) {
        this.type = type;
        this.toppings = new ArrayList<>(toppings);
        this.recipe = buildRecipe();
    }

    /**
     * Build complete recipe including base coffee and toppings
     */
    private Map<Ingredient, Integer> buildRecipe() {
        Map<Ingredient, Integer> fullRecipe = new HashMap<>(getBaseRecipe());
        
        // Add topping ingredients
        for (Topping topping : toppings) {
            fullRecipe.merge(topping.getIngredient(), topping.getQuantity(), Integer::sum);
        }
        
        return fullRecipe;
    }

    /**
     * Get base recipe for the coffee type
     */
    private Map<Ingredient, Integer> getBaseRecipe() {
        switch (type) {
            case ESPRESSO:
                return Map.of(
                    Ingredient.COFFEE_BEANS, 10,
                    Ingredient.WATER, 30
                );
            case LATTE:
                return Map.of(
                    Ingredient.COFFEE_BEANS, 8,
                    Ingredient.WATER, 30,
                    Ingredient.MILK, 150
                );
            case CAPPUCCINO:
                return Map.of(
                    Ingredient.COFFEE_BEANS, 8,
                    Ingredient.WATER, 30,
                    Ingredient.MILK, 100
                );
            case AMERICANO:
                return Map.of(
                    Ingredient.COFFEE_BEANS, 8,
                    Ingredient.WATER, 100
                );
            case MOCHA:
                return Map.of(
                    Ingredient.COFFEE_BEANS, 8,
                    Ingredient.WATER, 30,
                    Ingredient.MILK, 100,
                    Ingredient.CHOCOLATE, 20
                );
            default:
                throw new IllegalArgumentException("Unknown coffee type: " + type);
        }
    }

    /**
     * Calculate total price including toppings
     */
    public int getTotalPrice() {
        int basePrice = type.getBasePrice();
        int toppingCost = toppings.stream()
                .mapToInt(Topping::getPrice)
                .sum();
        return basePrice + toppingCost;
    }

    public CoffeeType getType() {
        return type;
    }

    public List<Topping> getToppings() {
        return Collections.unmodifiableList(toppings);
    }

    public Map<Ingredient, Integer> getRecipe() {
        return Collections.unmodifiableMap(recipe);
    }

    public String getDescription() {
        StringBuilder desc = new StringBuilder(type.getDisplayName());
        if (!toppings.isEmpty()) {
            desc.append(" with ");
            for (int i = 0; i < toppings.size(); i++) {
                desc.append(toppings.get(i).getDisplayName());
                if (i < toppings.size() - 1) {
                    desc.append(", ");
                }
            }
        }
        return desc.toString();
    }

    @Override
    public String toString() {
        return "Coffee{" +
                "type=" + type +
                ", toppings=" + toppings +
                ", price=" + getTotalPrice() +
                '}';
    }
}
