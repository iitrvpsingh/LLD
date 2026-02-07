package coffeemachine_patterns;

import coffeemachine_patterns.decorator.Coffee;
import coffeemachine_patterns.enums.PaymentMethod;
import coffeemachine_patterns.exception.CoffeeMachineException;
import coffeemachine_patterns.model.Transaction;
import coffeemachine_patterns.service.Inventory;
import coffeemachine_patterns.state.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Coffee Vending Machine using State Pattern
 * 
 * Design Patterns Used:
 * 1. State Pattern - For managing machine states (Idle, Selecting, Paid, Dispensing)
 * 2. Decorator Pattern - For adding toppings to coffee dynamically
 * 
 * The machine delegates all operations to the current state object.
 * Each state defines what operations are valid in that state.
 */
public class CoffeeMachine {
    
    // Current state (State Pattern)
    private VendingMachineState currentState;
    
    // Order details
    private Coffee selectedCoffee;
    private double moneyInserted;
    private PaymentMethod paymentMethod;
    
    // Services
    private final Inventory inventory;
    private final List<Transaction> transactionHistory;
    
    public CoffeeMachine(Inventory inventory) {
        this.inventory = inventory;
        this.transactionHistory = new ArrayList<>();
        this.currentState = new IdleState();
        this.moneyInserted = 0.0;
    }
    
    // ============ Public API (delegates to current state) ============
    
    /**
     * Select a coffee (can be decorated with toppings)
     */
    public void selectCoffee(Coffee coffee) throws CoffeeMachineException {
        currentState.selectCoffee(this, coffee);
    }
    
    /**
     * Insert money
     */
    public void insertMoney(double amount, PaymentMethod method) throws CoffeeMachineException {
        currentState.insertMoney(this, amount, method);
    }
    
    /**
     * Dispense coffee
     */
    public void dispense() throws CoffeeMachineException {
        currentState.dispense(this);
    }
    
    /**
     * Cancel current order
     */
    public void cancel() {
        currentState.cancel(this);
    }
    
    // ============ Public methods (used by states) ============
    
    public void setState(VendingMachineState state) {
        this.currentState = state;
    }
    
    public void setSelectedCoffee(Coffee coffee) {
        this.selectedCoffee = coffee;
    }
    
    public void addMoney(double amount) {
        this.moneyInserted += amount;
    }
    
    public void setPaymentMethod(PaymentMethod method) {
        this.paymentMethod = method;
    }
    
    public void reset() {
        this.selectedCoffee = null;
        this.moneyInserted = 0.0;
        this.paymentMethod = null;
    }
    
    public void recordTransaction(boolean successful) {
        if (selectedCoffee != null) {
            double change = moneyInserted - selectedCoffee.getCost();
            Transaction transaction = new Transaction(
                selectedCoffee,
                moneyInserted,
                Math.max(0, change),
                paymentMethod,
                successful
            );
            transactionHistory.add(transaction);
        }
    }
    
    // ============ Getters ============
    
    public String getCurrentStateName() {
        return currentState.getStateName();
    }
    
    public Coffee getSelectedCoffee() {
        return selectedCoffee;
    }
    
    public double getMoneyInserted() {
        return moneyInserted;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
    
    /**
     * Print machine status
     */
    public void printStatus() {
        System.out.println("\n=== Machine Status ===");
        System.out.println("State: " + currentState.getStateName());
        if (selectedCoffee != null) {
            System.out.println("Selected: " + selectedCoffee.getDescription());
            System.out.println("Price: " + selectedCoffee.getCost());
        }
        System.out.println("Money Inserted: " + moneyInserted);
        System.out.println("===================\n");
    }
}
