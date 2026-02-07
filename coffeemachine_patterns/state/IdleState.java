package coffeemachine_patterns.state;

import coffeemachine_patterns.CoffeeMachine;
import coffeemachine_patterns.decorator.Coffee;
import coffeemachine_patterns.enums.PaymentMethod;
import coffeemachine_patterns.exception.InvalidStateException;

/**
 * Idle State - Machine is ready for new order
 * Valid operations: selectCoffee
 */
public class IdleState implements VendingMachineState {
    
    @Override
    public void selectCoffee(CoffeeMachine machine, Coffee coffee) {
        machine.setSelectedCoffee(coffee);
        machine.setState(new SelectingState());
        System.out.println("\n☕ Selected: " + coffee.getDescription());
        System.out.println("💰 Price: " + coffee.getCost());
        System.out.println("📝 Please insert money...");
    }
    
    @Override
    public void insertMoney(CoffeeMachine machine, double amount, PaymentMethod method) 
            throws InvalidStateException {
        throw new InvalidStateException("Please select a coffee first");
    }
    
    @Override
    public void dispense(CoffeeMachine machine) throws InvalidStateException {
        throw new InvalidStateException("Please select a coffee first");
    }
    
    @Override
    public void cancel(CoffeeMachine machine) {
        System.out.println("ℹ️ No order to cancel");
    }
    
    @Override
    public String getStateName() {
        return "IDLE";
    }
}
