package coffeemachine;

import coffeemachine.enums.*;
import coffeemachine.exception.*;
import coffeemachine.model.*;
import coffeemachine.service.*;

import java.util.*;

/**
 * Main Coffee Vending Machine class
 * Clean, simple design without over-engineering
 * 
 * Design Principles:
 * - Single Responsibility: Each component has one job
 * - Dependency Injection: Services injected, not created internally
 * - Clear state management with enum (not State pattern - overkill)
 * - Proper error handling with exceptions
 * - Thread-safe operations
 */
public class CoffeeMachine {
    
    // Machine state (simple enum - no need for State pattern here)
    private enum State {
        IDLE,           // Ready for new order
        SELECTED,       // Coffee selected, waiting for payment
        PAID,           // Payment received, ready to dispense
        DISPENSING      // Currently dispensing
    }

    private State currentState;
    private Coffee selectedCoffee;
    private int moneyInserted;
    private PaymentMethod paymentMethod;
    
    // Services (injected for testability)
    private final Inventory inventory;
    private final PaymentProcessor paymentProcessor;
    private final List<Transaction> transactionHistory;

    public CoffeeMachine(Inventory inventory, PaymentProcessor paymentProcessor) {
        this.inventory = inventory;
        this.paymentProcessor = paymentProcessor;
        this.transactionHistory = new ArrayList<>();
        this.currentState = State.IDLE;
        this.moneyInserted = 0;
    }

    /**
     * Select coffee with optional toppings
     */
    public void selectCoffee(CoffeeType type, List<Topping> toppings) 
            throws InvalidStateException {
        
        if (currentState != State.IDLE) {
            throw new InvalidStateException("Please complete or cancel current order first");
        }

        this.selectedCoffee = new Coffee(type, toppings);
        this.currentState = State.SELECTED;
        
        System.out.println("\n☕ Selected: " + selectedCoffee.getDescription());
        System.out.println("💰 Price: " + selectedCoffee.getTotalPrice());
        System.out.println("📝 Please insert money...");
    }

    /**
     * Insert money
     */
    public void insertMoney(int amount, PaymentMethod method) 
            throws InvalidStateException {
        
        if (currentState != State.SELECTED) {
            throw new InvalidStateException("Please select a coffee first");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.moneyInserted += amount;
        this.paymentMethod = method;
        
        System.out.println("💵 Inserted: " + amount + " (Total: " + moneyInserted + ")");

        // Check if enough money
        if (moneyInserted >= selectedCoffee.getTotalPrice()) {
            this.currentState = State.PAID;
            System.out.println("✓ Payment complete. Ready to dispense.");
        } else {
            int remaining = selectedCoffee.getTotalPrice() - moneyInserted;
            System.out.println("📝 Please insert " + remaining + " more");
        }
    }

    /**
     * Dispense coffee
     */
    public Transaction dispense() throws CoffeeMachineException {
        
        if (currentState != State.PAID) {
            throw new InvalidStateException("Payment required before dispensing");
        }

        this.currentState = State.DISPENSING;

        try {
            // Step 1: Check and reserve ingredients
            if (!inventory.hasIngredients(selectedCoffee.getRecipe())) {
                System.out.println("❌ Out of ingredients for " + selectedCoffee.getDescription());
                refundAndReset();
                throw new OutOfStockException("Insufficient ingredients");
            }

            // Step 2: Process payment
            int change = paymentProcessor.processPayment(
                selectedCoffee.getTotalPrice(),
                moneyInserted,
                paymentMethod
            );

            // Step 3: Deduct ingredients
            inventory.reserveAndDeduct(selectedCoffee.getRecipe());

            // Step 4: Prepare coffee
            prepareCoffee(selectedCoffee);

            // Step 5: Dispense change
            paymentProcessor.dispenseChange(change);

            // Step 6: Record transaction
            Transaction transaction = new Transaction(
                selectedCoffee,
                moneyInserted,
                change,
                paymentMethod,
                true
            );
            transactionHistory.add(transaction);

            System.out.println("✅ Enjoy your " + selectedCoffee.getDescription() + "!");

            // Reset for next order
            reset();

            return transaction;

        } catch (OutOfStockException e) {
            // Already handled in refundAndReset()
            throw e;
        } catch (Exception e) {
            // Any other error - refund and reset
            System.err.println("❌ Error dispensing coffee: " + e.getMessage());
            refundAndReset();
            throw new CoffeeMachineException("Dispensing failed", e);
        }
    }

    /**
     * Cancel current order and refund
     */
    public void cancel() {
        if (currentState == State.IDLE) {
            System.out.println("ℹ️ No order to cancel");
            return;
        }

        System.out.println("🚫 Order cancelled");
        refundAndReset();
    }

    /**
     * Prepare coffee (simulate preparation steps)
     */
    private void prepareCoffee(Coffee coffee) {
        System.out.println("\n☕ Preparing " + coffee.getDescription() + "...");
        System.out.println("   1. Grinding fresh coffee beans");
        System.out.println("   2. Heating water");
        System.out.println("   3. Brewing coffee");
        
        // Add specific steps based on coffee type
        switch (coffee.getType()) {
            case LATTE:
            case CAPPUCCINO:
            case MOCHA:
                System.out.println("   4. Steaming milk");
                break;
        }

        // Add topping steps
        for (Topping topping : coffee.getToppings()) {
            System.out.println("   + Adding " + topping.getDisplayName());
        }

        System.out.println("   5. Pouring into cup");
        System.out.println("☕ " + coffee.getDescription() + " is ready!\n");
    }

    /**
     * Refund money and reset machine
     */
    private void refundAndReset() {
        if (moneyInserted > 0) {
            paymentProcessor.refund(moneyInserted, paymentMethod);
        }
        reset();
    }

    /**
     * Reset machine to idle state
     */
    private void reset() {
        this.selectedCoffee = null;
        this.moneyInserted = 0;
        this.paymentMethod = null;
        this.currentState = State.IDLE;
    }

    /**
     * Get current state
     */
    public String getCurrentState() {
        return currentState.name();
    }

    /**
     * Get selected coffee (if any)
     */
    public Coffee getSelectedCoffee() {
        return selectedCoffee;
    }

    /**
     * Get money inserted
     */
    public int getMoneyInserted() {
        return moneyInserted;
    }

    /**
     * Get transaction history
     */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    /**
     * Get inventory reference
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Print machine status
     */
    public void printStatus() {
        System.out.println("\n=== Machine Status ===");
        System.out.println("State: " + currentState);
        if (selectedCoffee != null) {
            System.out.println("Selected: " + selectedCoffee.getDescription());
            System.out.println("Price: " + selectedCoffee.getTotalPrice());
        }
        System.out.println("Money Inserted: " + moneyInserted);
        System.out.println("===================\n");
    }
}
