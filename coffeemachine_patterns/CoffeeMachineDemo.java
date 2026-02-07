package coffeemachine_patterns;

import coffeemachine_patterns.decorator.*;
import coffeemachine_patterns.enums.*;
import coffeemachine_patterns.exception.CoffeeMachineException;
import coffeemachine_patterns.service.Inventory;

/**
 * Demo showcasing State Pattern and Decorator Pattern
 */
public class CoffeeMachineDemo {

    public static void main(String[] args) {
        System.out.println("=== Coffee Vending Machine with State & Decorator Patterns ===\n");

        // Initialize
        Inventory inventory = new Inventory();
        CoffeeMachine machine = new CoffeeMachine(inventory);

        // Setup inventory
        setupInventory(inventory);

        try {
            // Scenario 1: Simple coffee with Decorator Pattern
            System.out.println("\n--- SCENARIO 1: Decorator Pattern Demo ---");
            scenario1_DecoratorPattern(machine);

            // Scenario 2: State Pattern - Invalid operations
            System.out.println("\n--- SCENARIO 2: State Pattern - Invalid Operations ---");
            scenario2_StatePattern(machine);

            // Scenario 3: Complex decorated coffee
            System.out.println("\n--- SCENARIO 3: Multiple Decorators ---");
            scenario3_MultipleDecorators(machine);

            // Scenario 4: Cancel order (State transition)
            System.out.println("\n--- SCENARIO 4: State Transition - Cancel ---");
            scenario4_CancelOrder(machine);

            // Final summary
            System.out.println("\n--- SUMMARY ---");
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

    private static void scenario1_DecoratorPattern(CoffeeMachine machine) 
            throws CoffeeMachineException {
        
        System.out.println("\n📦 Demonstrating Decorator Pattern:");
        System.out.println("Building: Latte + Extra Sugar + Caramel Syrup\n");

        // Start with base coffee
        Coffee coffee = new Latte();
        System.out.println("1. Base: " + coffee.getDescription() + " - Cost: " + coffee.getCost());

        // Decorate with Extra Sugar
        coffee = new ExtraSugarDecorator(coffee);
        System.out.println("2. Add Extra Sugar: " + coffee.getDescription() + " - Cost: " + coffee.getCost());

        // Decorate with Caramel Syrup
        coffee = new CaramelSyrupDecorator(coffee);
        System.out.println("3. Add Caramel: " + coffee.getDescription() + " - Cost: " + coffee.getCost());

        System.out.println("\n✨ Final decorated coffee: " + coffee.getDescription());
        System.out.println("💰 Total cost: " + coffee.getCost());

        // Now order it
        machine.selectCoffee(coffee);
        machine.insertMoney(300, PaymentMethod.CASH);
        machine.dispense();

        machine.getInventory().printInventory();
    }

    private static void scenario2_StatePattern(CoffeeMachine machine) {
        
        System.out.println("\n📦 Demonstrating State Pattern:");
        System.out.println("Current State: " + machine.getCurrentStateName());

        // Try invalid operations
        try {
            System.out.println("\n❌ Trying to dispense without selecting coffee...");
            machine.dispense();
        } catch (CoffeeMachineException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }

        try {
            System.out.println("\n❌ Trying to insert money without selecting coffee...");
            machine.insertMoney(100, PaymentMethod.CASH);
        } catch (CoffeeMachineException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }

        System.out.println("\n✓ State Pattern enforces valid operations per state!");
    }

    private static void scenario3_MultipleDecorators(CoffeeMachine machine) 
            throws CoffeeMachineException {
        
        System.out.println("\n📦 Creating heavily decorated coffee:");
        
        // Build: Cappuccino + Extra Sugar + Caramel + Whipped Cream
        Coffee coffee = new Cappuccino();
        coffee = new ExtraSugarDecorator(coffee);
        coffee = new CaramelSyrupDecorator(coffee);
        coffee = new WhippedCreamDecorator(coffee);

        System.out.println("☕ " + coffee.getDescription());
        System.out.println("💰 Cost: " + coffee.getCost());
        System.out.println("📋 Recipe: " + coffee.getRecipe());

        machine.selectCoffee(coffee);
        machine.insertMoney(300, PaymentMethod.CARD);
        machine.dispense();
    }

    private static void scenario4_CancelOrder(CoffeeMachine machine) 
            throws CoffeeMachineException {
        
        System.out.println("\n📦 Demonstrating state transition on cancel:");
        
        Coffee coffee = new Espresso();
        coffee = new ExtraSugarDecorator(coffee);
        
        machine.selectCoffee(coffee);
        System.out.println("State after selection: " + machine.getCurrentStateName());
        
        machine.insertMoney(100, PaymentMethod.CASH);
        System.out.println("State after partial payment: " + machine.getCurrentStateName());
        
        machine.cancel();
        System.out.println("State after cancel: " + machine.getCurrentStateName());
    }

    private static void printSummary(CoffeeMachine machine) {
        System.out.println("\n📊 Transaction Summary:");
        System.out.println("Total transactions: " + machine.getTransactionHistory().size());
        
        System.out.println("\n📋 Transaction History:");
        machine.getTransactionHistory().forEach(t -> System.out.println("   " + t));
        
        System.out.println("\n📦 Final Inventory:");
        machine.getInventory().printInventory();
        
        System.out.println("\n🎯 Design Patterns Demonstrated:");
        System.out.println("   ✓ State Pattern - Machine state management");
        System.out.println("   ✓ Decorator Pattern - Dynamic coffee customization");
    }
}
