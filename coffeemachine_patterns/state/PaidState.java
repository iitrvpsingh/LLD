package coffeemachine_patterns.state;

import coffeemachine_patterns.CoffeeMachine;
import coffeemachine_patterns.decorator.Coffee;
import coffeemachine_patterns.enums.PaymentMethod;
import coffeemachine_patterns.exception.CoffeeMachineException;
import coffeemachine_patterns.exception.InvalidStateException;

/**
 * Paid State - Payment received, ready to dispense
 * Valid operations: dispense, cancel
 */
public class PaidState implements VendingMachineState {
    
    @Override
    public void selectCoffee(CoffeeMachine machine, Coffee coffee) 
            throws InvalidStateException {
        throw new InvalidStateException("Please complete or cancel current order first");
    }
    
    @Override
    public void insertMoney(CoffeeMachine machine, double amount, PaymentMethod method) {
        System.out.println("ℹ️ Payment already complete. Extra money will be returned as change.");
        machine.addMoney(amount);
    }
    
    @Override
    public void dispense(CoffeeMachine machine) throws CoffeeMachineException {
        machine.setState(new DispensingState());
        
        // Check inventory
        if (!machine.getInventory().hasIngredients(machine.getSelectedCoffee().getRecipe())) {
            System.out.println("❌ Out of ingredients");
            cancel(machine);
            return;
        }
        
        // Deduct ingredients
        machine.getInventory().reserveAndDeduct(machine.getSelectedCoffee().getRecipe());
        
        // Prepare coffee
        System.out.println("\n☕ Preparing " + machine.getSelectedCoffee().getDescription() + "...");
        machine.getSelectedCoffee().prepare();
        System.out.println("☕ " + machine.getSelectedCoffee().getDescription() + " is ready!\n");
        
        // Calculate and dispense change
        double change = machine.getMoneyInserted() - machine.getSelectedCoffee().getCost();
        if (change > 0) {
            System.out.println("💰 Dispensing change: " + change);
        }
        
        System.out.println("✅ Enjoy your " + machine.getSelectedCoffee().getDescription() + "!");
        
        // Record transaction
        machine.recordTransaction(true);
        
        // Reset to idle
        machine.reset();
        machine.setState(new IdleState());
    }
    
    @Override
    public void cancel(CoffeeMachine machine) {
        System.out.println("🚫 Order cancelled");
        System.out.println("💰 Refunding " + machine.getMoneyInserted());
        machine.recordTransaction(false);
        machine.reset();
        machine.setState(new IdleState());
    }
    
    @Override
    public String getStateName() {
        return "PAID";
    }
}
