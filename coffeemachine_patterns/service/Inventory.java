package coffeemachine_patterns.service;

import coffeemachine_patterns.enums.Ingredient;
import coffeemachine_patterns.exception.OutOfStockException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inventory management service
 */
public class Inventory {
    private final Map<Ingredient, Integer> stock;

    public Inventory() {
        this.stock = new ConcurrentHashMap<>();
    }

    public synchronized void addStock(Ingredient ingredient, int quantity) {
        stock.merge(ingredient, quantity, Integer::sum);
    }

    public boolean hasIngredients(Map<Ingredient, Integer> recipe) {
        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            int available = stock.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public synchronized void reserveAndDeduct(Map<Ingredient, Integer> recipe) 
            throws OutOfStockException {
        // Check all ingredients first
        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            int available = stock.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) {
                throw new OutOfStockException(entry.getKey());
            }
        }

        // Then deduct all (atomic)
        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            stock.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        }
    }

    public int getStock(Ingredient ingredient) {
        return stock.getOrDefault(ingredient, 0);
    }

    public void printInventory() {
        System.out.println("=== Current Inventory ===");
        for (Ingredient ingredient : Ingredient.values()) {
            int level = stock.getOrDefault(ingredient, 0);
            System.out.printf("  %s: %d%n", ingredient, level);
        }
        System.out.println("========================");
    }
}
