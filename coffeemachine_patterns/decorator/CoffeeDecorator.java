package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Decorator for Coffee
 * Base class for all coffee decorators (toppings)
 */
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
    
    @Override
    public Map<Ingredient, Integer> getRecipe() {
        // Merge base recipe with decorator's ingredients
        Map<Ingredient, Integer> recipe = new HashMap<>(decoratedCoffee.getRecipe());
        Map<Ingredient, Integer> additionalIngredients = getAdditionalIngredients();
        
        for (Map.Entry<Ingredient, Integer> entry : additionalIngredients.entrySet()) {
            recipe.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        
        return recipe;
    }
    
    @Override
    public void prepare() {
        decoratedCoffee.prepare();
    }
    
    /**
     * Get additional ingredients added by this decorator
     */
    protected abstract Map<Ingredient, Integer> getAdditionalIngredients();
}
