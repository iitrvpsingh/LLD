package coffeemachine.service;

import coffeemachine.enums.Ingredient;
import coffeemachine.exception.OutOfStockException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages ingredient inventory
 * Thread-safe for concurrent operations
 */
public class Inventory {
    private final Map<Ingredient, Integer> stock;
    private final Map<Ingredient, Integer> minLevels;

    public Inventory() {
        this.stock = new ConcurrentHashMap<>();
        this.minLevels = new HashMap<>();
        initializeMinLevels();
    }

    private void initializeMinLevels() {
        // Set minimum stock levels for alerts
        minLevels.put(Ingredient.COFFEE_BEANS, 20);
        minLevels.put(Ingredient.WATER, 100);
        minLevels.put(Ingredient.MILK, 50);
        minLevels.put(Ingredient.SUGAR, 20);
    }

    /**
     * Add stock to inventory
     */
    public synchronized void addStock(Ingredient ingredient, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stock.merge(ingredient, quantity, Integer::sum);
    }

    /**
     * Check if ingredients are available
     */
    public boolean hasIngredients(Map<Ingredient, Integer> recipe) {
        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            int available = stock.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reserve and deduct ingredients atomically
     * Thread-safe to prevent race conditions
     */
    public synchronized void reserveAndDeduct(Map<Ingredient, Integer> recipe) 
            throws OutOfStockException {
        // First check if all ingredients available
        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            int available = stock.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) {
                throw new OutOfStockException(entry.getKey());
            }
        }

        // Then deduct all (atomic operation)
        for (Map.Entry<Ingredient, Integer> entry : recipe.entrySet()) {
            stock.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        }
    }

    /**
     * Get current stock level
     */
    public int getStock(Ingredient ingredient) {
        return stock.getOrDefault(ingredient, 0);
    }

    /**
     * Check if ingredient is below minimum level
     */
    public boolean isLowStock(Ingredient ingredient) {
        int current = stock.getOrDefault(ingredient, 0);
        int minimum = minLevels.getOrDefault(ingredient, 0);
        return current < minimum;
    }

    /**
     * Get all low stock ingredients
     */
    public Map<Ingredient, Integer> getLowStockItems() {
        Map<Ingredient, Integer> lowStock = new HashMap<>();
        for (Ingredient ingredient : Ingredient.values()) {
            if (isLowStock(ingredient)) {
                lowStock.put(ingredient, stock.getOrDefault(ingredient, 0));
            }
        }
        return lowStock;
    }

    /**
     * Get all stock levels
     */
    public Map<Ingredient, Integer> getAllStock() {
        return new HashMap<>(stock);
    }

    /**
     * Print inventory status
     */
    public void printInventory() {
        System.out.println("=== Current Inventory ===");
        for (Ingredient ingredient : Ingredient.values()) {
            int level = stock.getOrDefault(ingredient, 0);
            String status = isLowStock(ingredient) ? " ⚠️ LOW" : "";
            System.out.printf("  %s: %d%s%n", ingredient, level, status);
        }
        System.out.println("========================");
    }
}
