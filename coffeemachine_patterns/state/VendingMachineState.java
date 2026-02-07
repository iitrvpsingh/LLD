package coffeemachine_patterns.state;

import coffeemachine_patterns.CoffeeMachine;
import coffeemachine_patterns.decorator.Coffee;
import coffeemachine_patterns.enums.PaymentMethod;
import coffeemachine_patterns.exception.CoffeeMachineException;

/**
 * State Pattern - Interface for all vending machine states
 * Each state defines what operations are valid in that state
 */
public interface VendingMachineState {
    
    /**
     * Select a coffee
     */
    void selectCoffee(CoffeeMachine machine, Coffee coffee) throws CoffeeMachineException;
    
    /**
     * Insert money
     */
    void insertMoney(CoffeeMachine machine, double amount, PaymentMethod method) throws CoffeeMachineException;
    
    /**
     * Dispense coffee
     */
    void dispense(CoffeeMachine machine) throws CoffeeMachineException;
    
    /**
     * Cancel order
     */
    void cancel(CoffeeMachine machine);
    
    /**
     * Get state name for display
     */
    String getStateName();
}
