package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.Map;

/**
 * Base Coffee interface for Decorator Pattern
 * All coffee types and decorators implement this
 */
public interface Coffee {
    /**
     * Get description of the coffee
     */
    String getDescription();
    
    /**
     * Get cost of the coffee
     */
    double getCost();
    
    /**
     * Get recipe (ingredients and quantities needed)
     */
    Map<Ingredient, Integer> getRecipe();
    
    /**
     * Prepare the coffee (for display)
     */
    void prepare();
}
