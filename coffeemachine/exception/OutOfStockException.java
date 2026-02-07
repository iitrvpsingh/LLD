package coffeemachine.exception;

import coffeemachine.enums.Ingredient;

public class OutOfStockException extends CoffeeMachineException {
    public OutOfStockException(Ingredient ingredient) {
        super("Out of stock: " + ingredient);
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
