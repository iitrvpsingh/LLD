package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Coffee - Cappuccino
 */
public class Cappuccino implements Coffee {
    
    @Override
    public String getDescription() {
        return "Cappuccino";
    }
    
    @Override
    public double getCost() {
        return 200.0;
    }
    
    @Override
    public Map<Ingredient, Integer> getRecipe() {
        Map<Ingredient, Integer> recipe = new HashMap<>();
        recipe.put(Ingredient.COFFEE_BEANS, 8);
        recipe.put(Ingredient.WATER, 30);
        recipe.put(Ingredient.MILK, 100);
        return recipe;
    }
    
    @Override
    public void prepare() {
        System.out.println("   - Grinding 8g coffee beans");
        System.out.println("   - Adding 30ml hot water");
        System.out.println("   - Steaming 100ml milk");
        System.out.println("   - Brewing cappuccino");
    }
}
