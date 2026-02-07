package coffeemachine_patterns.exception;

public class InsufficientFundsException extends CoffeeMachineException {
    public InsufficientFundsException(double required, double provided) {
        super("Insufficient funds. Required: " + required + ", Provided: " + provided);
    }
}
