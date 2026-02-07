package coffeemachine_patterns.state;

import coffeemachine_patterns.CoffeeMachine;
import coffeemachine_patterns.decorator.Coffee;
import coffeemachine_patterns.enums.PaymentMethod;
import coffeemachine_patterns.exception.InvalidStateException;

/**
 * Dispensing State - Currently dispensing coffee
 * No operations allowed during dispensing
 */
public class DispensingState implements VendingMachineState {
    
    @Override
    public void selectCoffee(CoffeeMachine machine, Coffee coffee) 
            throws InvalidStateException {
        throw new InvalidStateException("Please wait, dispensing in progress");
    }
    
    @Override
    public void insertMoney(CoffeeMachine machine, double amount, PaymentMethod method) 
            throws InvalidStateException {
        throw new InvalidStateException("Please wait, dispensing in progress");
    }
    
    @Override
    public void dispense(CoffeeMachine machine) throws InvalidStateException {
        throw new InvalidStateException("Already dispensing");
    }
    
    @Override
    public void cancel(CoffeeMachine machine) {
        System.out.println("⚠️ Cannot cancel during dispensing");
    }
    
    @Override
    public String getStateName() {
        return "DISPENSING";
    }
}
