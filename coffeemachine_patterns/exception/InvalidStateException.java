package coffeemachine_patterns.exception;

public class InvalidStateException extends CoffeeMachineException {
    public InvalidStateException(String message) {
        super(message);
    }
}
