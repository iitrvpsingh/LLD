package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Coffee - Latte
 */
public class Latte implements Coffee {
    
    @Override
    public String getDescription() {
        return "Latte";
    }
    
    @Override
    public double getCost() {
        return 220.0;
    }
    
    @Override
    public Map<Ingredient, Integer> getRecipe() {
        Map<Ingredient, Integer> recipe = new HashMap<>();
        recipe.put(Ingredient.COFFEE_BEANS, 8);
        recipe.put(Ingredient.WATER, 30);
        recipe.put(Ingredient.MILK, 150);
        return recipe;
    }
    
    @Override
    public void prepare() {
        System.out.println("   - Grinding 8g coffee beans");
        System.out.println("   - Adding 30ml hot water");
        System.out.println("   - Steaming 150ml milk");
        System.out.println("   - Brewing latte");
    }
}
