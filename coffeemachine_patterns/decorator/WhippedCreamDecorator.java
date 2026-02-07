package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Decorator - Whipped Cream
 */
public class WhippedCreamDecorator extends CoffeeDecorator {
    
    public WhippedCreamDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Whipped Cream";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 15.0;
    }
    
    @Override
    protected Map<Ingredient, Integer> getAdditionalIngredients() {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(Ingredient.WHIPPED_CREAM, 20);
        return ingredients;
    }
    
    @Override
    public void prepare() {
        decoratedCoffee.prepare();
        System.out.println("   - Topping with whipped cream (20g)");
    }
}
