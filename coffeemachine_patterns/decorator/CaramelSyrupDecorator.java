package coffeemachine_patterns.decorator;

import coffeemachine_patterns.enums.Ingredient;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Decorator - Caramel Syrup
 */
public class CaramelSyrupDecorator extends CoffeeDecorator {
    
    public CaramelSyrupDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Caramel Syrup";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 20.0;
    }
    
    @Override
    protected Map<Ingredient, Integer> getAdditionalIngredients() {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(Ingredient.CARAMEL_SYRUP, 10);
        return ingredients;
    }
    
    @Override
    public void prepare() {
        decoratedCoffee.prepare();
        System.out.println("   - Adding caramel syrup (10ml)");
    }
}
