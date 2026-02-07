package coffeemachine;

import coffeemachine.enums.*;
import coffeemachine.exception.*;
import coffeemachine.model.*;
import coffeemachine.service.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Demo of the clean Coffee Machine design
 * Shows practical usage without over-engineering
 */
public class CoffeeMachineDemo {

    public static void main(String[] args) {
        System.out.println("=== Coffee Vending Machine Demo ===\n");

        // Initialize machine with services (Dependency Injection)
        Inventory inventory = new Inventory();
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        CoffeeMachine machine = new CoffeeMachine(inventory, paymentProcessor);

        // Initial stock
        setupInventory(inventory);

        try {
            // Scenario 1: Successful purchase
            System.out.println("\n--- SCENARIO 1: Buy Latte with Extra Sugar (Success) ---");
            scenario1_SuccessfulPurchase(machine);

            // Scenario 2: Insufficient funds and cancellation
            System.out.println("\n--- SCENARIO 2: Insufficient Funds & Cancel ---");
            scenario2_InsufficientFunds(machine);

            // Scenario 3: Out of stock
            System.out.println("\n--- SCENARIO 3: Out of Stock ---");
            scenario3_OutOfStock(machine, inventory);

            // Scenario 4: Concurrent orders
            System.out.println("\n--- SCENARIO 4: Concurrent Orders (Thread Safety) ---");
            scenario4_ConcurrentOrders(inventory, paymentProcessor);

            // Final summary
            System.out.println("\n--- FINAL SUMMARY ---");
            printSummary(machine);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Demo Complete ===");
    }

    private static void setupInventory(Inventory inventory) {
        System.out.println("🔧 Setting up inventory...");
        inventory.addStock(Ingredient.COFFEE_BEANS, 100);
        inventory.addStock(Ingredient.WATER, 500);
        inventory.addStock(Ingredient.MILK, 300);
        inventory.addStock(Ingredient.SUGAR, 100);
        inventory.addStock(Ingredient.CHOCOLATE, 50);
        inventory.addStock(Ingredient.CARAMEL_SYRUP, 50);
        inventory.addStock(Ingredient.WHIPPED_CREAM, 50);
        inventory.printInventory();
    }

    private static void scenario1_SuccessfulPurchase(CoffeeMachine machine) 
            throws CoffeeMachineException {
        
        // Select coffee with toppings
        machine.selectCoffee(
            CoffeeType.LATTE, 
            Arrays.asList(Topping.EXTRA_SUGAR, Topping.CARAMEL_SYRUP)
        );

        // Insert money
        machine.insertMoney(200, PaymentMethod.CASH);
        machine.insertMoney(100, PaymentMethod.CASH); // Total: 300

        // Dispense
        Transaction transaction = machine.dispense();
        System.out.println("📄 Transaction: " + transaction);

        machine.getInventory().printInventory();
    }

    private static void scenario2_InsufficientFunds(CoffeeMachine machine) 
            throws InvalidStateException {
        
        machine.selectCoffee(CoffeeType.ESPRESSO, Collections.emptyList());
        machine.insertMoney(100, PaymentMethod.CASH); // Need 150

        try {
            machine.dispense(); // Should fail
        } catch (CoffeeMachineException e) {
            System.out.println("❌ Cannot dispense: " + e.getMessage());
        }

        // Cancel and get refund
        machine.cancel();
    }

    private static void scenario3_OutOfStock(CoffeeMachine machine, Inventory inventory) 
            throws InvalidStateException {
        
        // Drain milk to simulate out of stock
        System.out.println("🔧 Simulating low milk stock...");
        try {
            inventory.reserveAndDeduct(Map.of(Ingredient.MILK, 250));
        } catch (OutOfStockException e) {
            // Expected
        }
        inventory.printInventory();

        // Try to order latte (needs milk)
        machine.selectCoffee(CoffeeType.LATTE, Collections.emptyList());
        machine.insertMoney(250, PaymentMethod.CARD);

        try {
            machine.dispense(); // Should fail and refund
        } catch (CoffeeMachineException e) {
            System.out.println("✓ Correctly handled out of stock: " + e.getMessage());
        }

        // Refill
        System.out.println("\n🔧 Refilling milk...");
        inventory.addStock(Ingredient.MILK, 300);
        inventory.printInventory();
    }

    private static void scenario4_ConcurrentOrders(Inventory inventory, 
                                                   PaymentProcessor paymentProcessor) {
        System.out.println("🔄 Processing 10 concurrent orders...");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<OrderResult>> futures = new ArrayList<>();

        // Submit 10 concurrent orders
        for (int i = 1; i <= 10; i++) {
            final int orderNum = i;
            futures.add(executor.submit(() -> {
                try {
                    CoffeeMachine machine = new CoffeeMachine(inventory, paymentProcessor);
                    
                    CoffeeType type = CoffeeType.values()[orderNum % CoffeeType.values().length];
                    machine.selectCoffee(type, Collections.emptyList());
                    machine.insertMoney(300, PaymentMethod.CARD);
                    
                    Transaction transaction = machine.dispense();
                    return new OrderResult(orderNum, true, null);
                    
                } catch (Exception e) {
                    return new OrderResult(orderNum, false, e.getMessage());
                }
            }));
        }

        // Collect results
        int success = 0;
        int failed = 0;

        for (Future<OrderResult> future : futures) {
            try {
                OrderResult result = future.get();
                if (result.success) {
                    success++;
                } else {
                    failed++;
                    System.out.println("   ✗ Order " + result.orderNum + " failed: " + result.error);
                }
            } catch (Exception e) {
                failed++;
            }
        }

        executor.shutdown();

        System.out.println("   ✓ Successful orders: " + success);
        System.out.println("   ✗ Failed orders: " + failed);
        
        inventory.printInventory();
    }

    private static void printSummary(CoffeeMachine machine) {
        List<Transaction> history = machine.getTransactionHistory();
        
        System.out.println("📊 Transaction Summary:");
        System.out.println("   Total transactions: " + history.size());
        
        int totalRevenue = history.stream()
            .filter(Transaction::isSuccessful)
            .mapToInt(t -> t.getAmountPaid() - t.getChange())
            .sum();
        
        System.out.println("   Total revenue: " + totalRevenue);
        
        System.out.println("\n📋 Transaction History:");
        history.forEach(t -> System.out.println("   " + t));
        
        System.out.println("\n📦 Final Inventory:");
        machine.getInventory().printInventory();
        
        Map<Ingredient, Integer> lowStock = machine.getInventory().getLowStockItems();
        if (!lowStock.isEmpty()) {
            System.out.println("\n⚠️ Low Stock Alert:");
            lowStock.forEach((ingredient, level) -> 
                System.out.println("   " + ingredient + ": " + level));
        }
    }

    static class OrderResult {
        int orderNum;
        boolean success;
        String error;

        OrderResult(int orderNum, boolean success, String error) {
            this.orderNum = orderNum;
            this.success = success;
            this.error = error;
        }
    }
}
