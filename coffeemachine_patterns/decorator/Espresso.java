package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Coffee - Espresso
 */
public class Espresso implements Coffee {
    
    @Override
    public String getDescription() {
        return "Espresso";
    }
    
    @Override
    public double getCost() {
        return 150.0;
    }
    
    @Override
    public Map<Ingredient, Integer> getRecipe() {
        Map<Ingredient, Integer> recipe = new HashMap<>();
        recipe.put(Ingredient.COFFEE_BEANS, 10);
        recipe.put(Ingredient.WATER, 30);
        return recipe;
    }
    
    @Override
    public void prepare() {
        System.out.println("   - Grinding 10g coffee beans");
        System.out.println("   - Adding 30ml hot water");
        System.out.println("   - Brewing espresso");
    }
}
