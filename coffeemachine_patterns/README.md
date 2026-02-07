# Coffee Vending Machine - State & Decorator Patterns

## Overview

This is a **pattern-focused implementation** of a coffee vending machine that demonstrates:
- ✅ **State Pattern** - For managing machine states
- ✅ **Decorator Pattern** - For dynamically adding toppings to coffee

**Purpose**: Educational - shows proper usage of State and Decorator patterns

---

## Design Patterns

### 1. State Pattern

**Problem**: Machine behavior changes based on current state (Idle, Selecting, Paid, Dispensing)

**Solution**: Encapsulate state-specific behavior in separate state classes

```
VendingMachineState (Interface)
    ├── IdleState
    ├── SelectingState
    ├── PaidState
    └── DispensingState
```

**Benefits**:
- Each state defines valid operations
- State transitions are explicit
- Easy to add new states
- Eliminates complex if-else chains

**Example**:
```java
// In IdleState - only selectCoffee is valid
public void selectCoffee(CoffeeMachine machine, Coffee coffee) {
    machine.setSelectedCoffee(coffee);
    machine.setState(new SelectingState());  // Transition to next state
}

public void dispense(CoffeeMachine machine) throws InvalidStateException {
    throw new InvalidStateException("Please select a coffee first");
}
```

---

### 2. Decorator Pattern

**Problem**: Need to add toppings to coffee dynamically without modifying coffee classes

**Solution**: Wrap coffee objects with decorator objects

```
Coffee (Interface)
    ├── Espresso (Concrete)
    ├── Latte (Concrete)
    ├── Cappuccino (Concrete)
    └── CoffeeDecorator (Abstract)
            ├── ExtraSugarDecorator
            ├── CaramelSyrupDecorator
            └── WhippedCreamDecorator
```

**Benefits**:
- Add functionality at runtime
- Combine decorators freely
- Open/Closed Principle
- Single Responsibility

**Example**:
```java
// Build decorated coffee
Coffee coffee = new Latte();                          // Base: 220
coffee = new ExtraSugarDecorator(coffee);             // +10 = 230
coffee = new CaramelSyrupDecorator(coffee);           // +20 = 250

System.out.println(coffee.getDescription());  // "Latte, Extra Sugar, Caramel Syrup"
System.out.println(coffee.getCost());         // 250.0
```

---

## Architecture

```
┌─────────────────────────────────────┐
│        CoffeeMachine                │
│  - currentState: VendingMachineState│  ← State Pattern
│  - selectedCoffee: Coffee           │  ← Decorator Pattern
├─────────────────────────────────────┤
│  + selectCoffee(coffee)             │  ← Delegates to state
│  + insertMoney(amount, method)      │  ← Delegates to state
│  + dispense()                       │  ← Delegates to state
│  + cancel()                         │  ← Delegates to state
└──────────┬──────────────────────────┘
           │
    ┌──────┴──────┐
    │             │
┌───▼────┐   ┌───▼────────┐
│States  │   │Decorators  │
│        │   │            │
└────────┘   └────────────┘
```

---

## State Diagram

```
┌──────┐  selectCoffee   ┌───────────┐
│ IDLE │ ───────────────>│ SELECTING │
└──────┘                 └─────┬─────┘
   ↑                           │
   │                           │ insertMoney (enough)
   │                           ↓
   │                      ┌────────┐
   │        dispense      │  PAID  │
   │      <───────────────┤        │
   │                      └────────┘
   │                           │
   │                           │ dispense
   │                           ↓
   │                    ┌─────────────┐
   └────────────────────│ DISPENSING  │
         (auto)         └─────────────┘
```

---

## Package Structure

```
coffeemachine_patterns/
├── state/                           # State Pattern
│   ├── VendingMachineState.java    # State interface
│   ├── IdleState.java               # Ready for order
│   ├── SelectingState.java          # Waiting for payment
│   ├── PaidState.java               # Ready to dispense
│   └── DispensingState.java         # Currently dispensing
│
├── decorator/                       # Decorator Pattern
│   ├── Coffee.java                  # Component interface
│   ├── Espresso.java                # Concrete component
│   ├── Latte.java                   # Concrete component
│   ├── Cappuccino.java              # Concrete component
│   ├── CoffeeDecorator.java         # Decorator base
│   ├── ExtraSugarDecorator.java     # Concrete decorator
│   ├── CaramelSyrupDecorator.java   # Concrete decorator
│   └── WhippedCreamDecorator.java   # Concrete decorator
│
├── service/
│   └── Inventory.java               # Stock management
│
├── model/
│   └── Transaction.java             # Transaction record
│
├── enums/
│   ├── CoffeeType.java
│   ├── Ingredient.java
│   └── PaymentMethod.java
│
├── exception/
│   ├── CoffeeMachineException.java
│   ├── InsufficientFundsException.java
│   ├── OutOfStockException.java
│   └── InvalidStateException.java
│
├── CoffeeMachine.java               # Main controller
└── CoffeeMachineDemo.java           # Demo application
```

---

## Usage Example

### Basic Usage

```java
// Initialize
Inventory inventory = new Inventory();
inventory.addStock(Ingredient.COFFEE_BEANS, 100);
inventory.addStock(Ingredient.WATER, 500);
inventory.addStock(Ingredient.MILK, 300);

CoffeeMachine machine = new CoffeeMachine(inventory);

// Create decorated coffee using Decorator Pattern
Coffee coffee = new Latte();
coffee = new ExtraSugarDecorator(coffee);
coffee = new CaramelSyrupDecorator(coffee);

// Order using State Pattern
machine.selectCoffee(coffee);           // State: IDLE → SELECTING
machine.insertMoney(300, PaymentMethod.CASH);  // State: SELECTING → PAID
machine.dispense();                     // State: PAID → DISPENSING → IDLE
```

---

## State Pattern - Detailed

### State Interface

```java
public interface VendingMachineState {
    void selectCoffee(CoffeeMachine machine, Coffee coffee) throws CoffeeMachineException;
    void insertMoney(CoffeeMachine machine, double amount, PaymentMethod method) throws CoffeeMachineException;
    void dispense(CoffeeMachine machine) throws CoffeeMachineException;
    void cancel(CoffeeMachine machine);
    String getStateName();
}
```

### State Transitions

| Current State | Valid Operations | Next State |
|--------------|------------------|------------|
| **IDLE** | selectCoffee | SELECTING |
| **SELECTING** | insertMoney (partial) | SELECTING |
| **SELECTING** | insertMoney (enough) | PAID |
| **SELECTING** | cancel | IDLE |
| **PAID** | dispense | DISPENSING → IDLE |
| **PAID** | cancel | IDLE |
| **DISPENSING** | (none - auto transition) | IDLE |

### Invalid Operations

Each state throws `InvalidStateException` for invalid operations:

```java
// In IdleState
public void dispense(CoffeeMachine machine) throws InvalidStateException {
    throw new InvalidStateException("Please select a coffee first");
}

// In SelectingState
public void dispense(CoffeeMachine machine) throws InvalidStateException {
    throw new InvalidStateException("Payment required before dispensing");
}
```

---

## Decorator Pattern - Detailed

### Component Interface

```java
public interface Coffee {
    String getDescription();
    double getCost();
    Map<Ingredient, Integer> getRecipe();
    void prepare();
}
```

### Concrete Components

```java
public class Latte implements Coffee {
    public String getDescription() { return "Latte"; }
    public double getCost() { return 220.0; }
    // ... recipe and prepare methods
}
```

### Decorator Base Class

```java
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }
    
    public double getCost() {
        return decoratedCoffee.getCost();
    }
    
    // Merge recipes
    public Map<Ingredient, Integer> getRecipe() {
        Map<Ingredient, Integer> recipe = new HashMap<>(decoratedCoffee.getRecipe());
        Map<Ingredient, Integer> additional = getAdditionalIngredients();
        for (Map.Entry<Ingredient, Integer> entry : additional.entrySet()) {
            recipe.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        return recipe;
    }
    
    protected abstract Map<Ingredient, Integer> getAdditionalIngredients();
}
```

### Concrete Decorators

```java
public class ExtraSugarDecorator extends CoffeeDecorator {
    public ExtraSugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Extra Sugar";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 10.0;
    }
    
    @Override
    protected Map<Ingredient, Integer> getAdditionalIngredients() {
        return Map.of(Ingredient.SUGAR, 5);
    }
}
```

---

## Running the Demo

```bash
cd ~/Downloads/LLD
javac coffeemachine_patterns/**/*.java coffeemachine_patterns/*.java
java coffeemachine_patterns.CoffeeMachineDemo
```

---

## Demo Output

```
=== Coffee Vending Machine with State & Decorator Patterns ===

--- SCENARIO 1: Decorator Pattern Demo ---

📦 Demonstrating Decorator Pattern:
Building: Latte + Extra Sugar + Caramel Syrup

1. Base: Latte - Cost: 220.0
2. Add Extra Sugar: Latte, Extra Sugar - Cost: 230.0
3. Add Caramel: Latte, Extra Sugar, Caramel Syrup - Cost: 250.0

✨ Final decorated coffee: Latte, Extra Sugar, Caramel Syrup
💰 Total cost: 250.0

☕ Selected: Latte, Extra Sugar, Caramel Syrup
💰 Price: 250.0
...

--- SCENARIO 2: State Pattern - Invalid Operations ---

📦 Demonstrating State Pattern:
Current State: IDLE

❌ Trying to dispense without selecting coffee...
✓ Correctly rejected: Please select a coffee first

❌ Trying to insert money without selecting coffee...
✓ Correctly rejected: Please select a coffee first

✓ State Pattern enforces valid operations per state!
```

---

## When to Use These Patterns

### Use State Pattern When:
- ✅ Object behavior changes based on state
- ✅ Many state-specific operations
- ✅ State transitions are complex
- ✅ Want to eliminate large if-else chains
- ✅ Need to enforce valid operations per state

### Use Decorator Pattern When:
- ✅ Need to add responsibilities dynamically
- ✅ Want to combine features flexibly
- ✅ Inheritance is impractical (too many combinations)
- ✅ Need to wrap objects with additional behavior
- ✅ Open/Closed Principle is important

---

## Advantages

### State Pattern
- ✅ **Clarity**: Each state is a separate class
- ✅ **Maintainability**: Easy to add new states
- ✅ **Encapsulation**: State-specific behavior is isolated
- ✅ **Type Safety**: Compiler enforces state interface

### Decorator Pattern
- ✅ **Flexibility**: Combine decorators freely
- ✅ **Open/Closed**: Add new decorators without modifying existing code
- ✅ **Single Responsibility**: Each decorator has one job
- ✅ **Runtime Composition**: Build objects dynamically

---

## Comparison with Clean Version

| Aspect | This (Patterns) | Clean Version |
|--------|----------------|---------------|
| **State Management** | State Pattern (5 classes) | Enum (1 enum) |
| **Toppings** | Decorator Pattern (4+ classes) | List (1 class) |
| **Complexity** | Higher | Lower |
| **Flexibility** | Very high | Medium |
| **Maintainability** | Good (if states/decorators grow) | Better (for simple cases) |
| **Learning Value** | High (shows patterns) | Medium |
| **Production Use** | When complexity justifies it | Most cases |

---

## Interview Talking Points

### Q: When would you use State Pattern over simple enum?

**A**: "State Pattern is better when:
- You have 10+ states with complex transitions
- Each state has substantial state-specific behavior
- State behavior changes frequently
- You need to enforce valid operations per state at compile time

For this vending machine with 4 simple states, an enum is simpler. But if we had complex state machines (e.g., ATM with 15+ states, each with different authentication, transaction, and error handling logic), State Pattern would be better."

### Q: When would you use Decorator over simple list?

**A**: "Decorator is better when:
- You're adding behavior, not just data
- Need runtime composition of complex features
- Multiple layers of wrapping with interactions
- Example: I/O streams (BufferedInputStream wrapping FileInputStream)

For coffee toppings (just data), a list is simpler. But if toppings had complex behavior (e.g., temperature adjustments, preparation order dependencies), Decorator would be better."

---

## Summary

This implementation demonstrates:

✅ **State Pattern** - Proper state management with explicit transitions  
✅ **Decorator Pattern** - Dynamic composition of coffee with toppings  
✅ **SOLID Principles** - Each class has single responsibility  
✅ **Type Safety** - Compiler enforces valid operations  
✅ **Extensibility** - Easy to add new states or decorators  

**Perfect for showing you understand these patterns deeply!** 🎯
