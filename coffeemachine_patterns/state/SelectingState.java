package coffeemachine_patterns.state;

import coffeemachine_patterns.CoffeeMachine;
import coffeemachine_patterns.decorator.Coffee;
import coffeemachine_patterns.enums.PaymentMethod;
import coffeemachine_patterns.exception.InvalidStateException;

/**
 * Selecting State - Coffee selected, waiting for payment
 * Valid operations: insertMoney, cancel
 */
public class SelectingState implements VendingMachineState {
    
    @Override
    public void selectCoffee(CoffeeMachine machine, Coffee coffee) 
            throws InvalidStateException {
        throw new InvalidStateException("Please complete or cancel current order first");
    }
    
    @Override
    public void insertMoney(CoffeeMachine machine, double amount, PaymentMethod method) {
        if (amount <= 0) {
            System.out.println("❌ Invalid amount");
            return;
        }
        
        machine.addMoney(amount);
        machine.setPaymentMethod(method);
        
        System.out.println("💵 Inserted: " + amount + " (Total: " + machine.getMoneyInserted() + ")");
        
        double required = machine.getSelectedCoffee().getCost();
        double total = machine.getMoneyInserted();
        
        if (total >= required) {
            machine.setState(new PaidState());
            System.out.println("✓ Payment complete. Ready to dispense.");
        } else {
            double remaining = required - total;
            System.out.println("📝 Please insert " + remaining + " more");
        }
    }
    
    @Override
    public void dispense(CoffeeMachine machine) throws InvalidStateException {
        throw new InvalidStateException("Payment required before dispensing");
    }
    
    @Override
    public void cancel(CoffeeMachine machine) {
        System.out.println("🚫 Order cancelled");
        if (machine.getMoneyInserted() > 0) {
            System.out.println("💰 Refunding " + machine.getMoneyInserted());
        }
        machine.reset();
        machine.setState(new IdleState());
    }
    
    @Override
    public String getStateName() {
        return "SELECTING";
    }
}
