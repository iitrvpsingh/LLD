package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Decorator - Extra Sugar
 */
public class ExtraSugarDecorator extends CoffeeDecorator {
    
    public ExtraSugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Extra Sugar";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 10.0;
    }
    
    @Override
    protected Map<Ingredient, Integer> getAdditionalIngredients() {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(Ingredient.SUGAR, 5);
        return ingredients;
    }
    
    @Override
    public void prepare() {
        decoratedCoffee.prepare();
        System.out.println("   - Adding extra sugar (5g)");
    }
}
