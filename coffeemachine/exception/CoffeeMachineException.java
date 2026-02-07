package coffeemachine.exception;

/**
 * Base exception for coffee machine operations
 */
public class CoffeeMachineException extends Exception {
    public CoffeeMachineException(String message) {
        super(message);
    }

    public CoffeeMachineException(String message, Throwable cause) {
        super(message, cause);
    }
}
