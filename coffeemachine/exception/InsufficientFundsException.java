package coffeemachine.exception;

public class InsufficientFundsException extends CoffeeMachineException {
    public InsufficientFundsException(int required, int provided) {
        super("Insufficient funds. Required: " + required + ", Provided: " + provided);
    }
}
