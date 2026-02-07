package coffeemachine_patterns.exception;

import coffeemachine_patterns.enums.Ingredient;

public class OutOfStockException extends CoffeeMachineException {
    public OutOfStockException(Ingredient ingredient) {
        super("Out of stock: " + ingredient);
    }
}
