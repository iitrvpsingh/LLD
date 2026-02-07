# Clean Coffee Vending Machine Design

## Overview

This is a **clean, practical coffee vending machine** design that prioritizes:
- ✅ **Simplicity** over complexity
- ✅ **Clarity** over cleverness
- ✅ **Maintainability** over pattern showcase
- ✅ **Production-readiness** over academic exercise

**Design Philosophy**: Use patterns when they solve problems, not just to use patterns.

---

## Key Differences from Original Design

| Aspect | Original (Over-Engineered) | This (Clean) |
|--------|---------------------------|--------------|
| **State Management** | State Pattern (5 classes) | Simple enum |
| **Coffee Creation** | Factory Pattern | Direct instantiation |
| **Toppings** | Decorator Pattern | List of toppings |
| **Preparation** | Template Method | Simple method |
| **Complexity** | High (4 patterns) | Low (pragmatic) |
| **Lines of Code** | ~500 lines | ~300 lines |
| **Understandability** | Medium | High |
| **Production Ready** | No | Yes |

---

## Architecture

```
┌─────────────────────────────────────┐
│        CoffeeMachine                │
│  - currentState: State (enum)       │
│  - selectedCoffee: Coffee           │
│  - moneyInserted: int               │
├─────────────────────────────────────┤
│  + selectCoffee(type, toppings)     │
│  + insertMoney(amount, method)      │
│  + dispense(): Transaction          │
│  + cancel()                         │
└──────────┬──────────────────────────┘
           │
    ┌──────┴──────┐
    │             │
┌───▼────┐   ┌───▼────────┐
│Inventory│   │Payment     │
│         │   │Processor   │
└─────────┘   └────────────┘
```

---

## Package Structure

```
coffeemachine/
├── model/
│   ├── Coffee.java              # Coffee order with toppings
│   └── Transaction.java         # Transaction record
│
├── service/
│   ├── Inventory.java           # Ingredient management
│   └── PaymentProcessor.java   # Payment handling
│
├── enums/
│   ├── CoffeeType.java          # Coffee types with prices
│   ├── Ingredient.java          # Ingredients
│   ├── Topping.java             # Toppings with prices
│   └── PaymentMethod.java       # Payment methods
│
├── exception/
│   ├── CoffeeMachineException.java
│   ├── InsufficientFundsException.java
│   ├── OutOfStockException.java
│   └── InvalidStateException.java
│
├── CoffeeMachine.java           # Main machine class
└── CoffeeMachineDemo.java       # Demo application
```

---

## Design Principles

### 1. **Single Responsibility Principle** ✅

Each class has ONE job:
- `CoffeeMachine`: Coordinates the order flow
- `Inventory`: Manages ingredients
- `PaymentProcessor`: Handles payments
- `Coffee`: Represents a coffee order
- `Transaction`: Records transaction data

### 2. **Dependency Injection** ✅

```java
// Services are injected, not created internally
public CoffeeMachine(Inventory inventory, PaymentProcessor paymentProcessor) {
    this.inventory = inventory;
    this.paymentProcessor = paymentProcessor;
}

// Benefits:
// - Easy to test (can inject mocks)
// - Flexible (can swap implementations)
// - No tight coupling
```

### 3. **Proper Error Handling** ✅

```java
// Custom exception hierarchy
try {
    machine.dispense();
} catch (InsufficientFundsException e) {
    // Handle insufficient funds
} catch (OutOfStockException e) {
    // Handle out of stock
} catch (InvalidStateException e) {
    // Handle invalid state
}
```

### 4. **Thread Safety** ✅

```java
// Synchronized methods for critical sections
public synchronized void reserveAndDeduct(Map<Ingredient, Integer> recipe) {
    // Atomic check-and-deduct
}
```

### 5. **Immutability Where Appropriate** ✅

```java
// Transaction is immutable (audit trail)
public class Transaction {
    private final String transactionId;
    private final Coffee coffee;
    private final int amountPaid;
    // No setters
}
```

---

## Key Features

### 1. **Simple State Management**

```java
private enum State {
    IDLE,       // Ready for new order
    SELECTED,   // Coffee selected, waiting for payment
    PAID,       // Payment received, ready to dispense
    DISPENSING  // Currently dispensing
}
```

**Why Enum Instead of State Pattern?**
- Simpler to understand
- Less code to maintain
- Sufficient for linear workflow
- State Pattern is overkill here

**When to Use State Pattern**:
- Complex state transitions
- Many states (10+)
- State-specific behavior is complex

---

### 2. **Clean Coffee Model**

```java
public class Coffee {
    private final CoffeeType type;
    private final List<Topping> toppings;
    
    public int getTotalPrice() {
        return type.getBasePrice() + 
               toppings.stream().mapToInt(Topping::getPrice).sum();
    }
}
```

**Why No Decorator Pattern?**
- Toppings are simple data, not behavior
- List is clearer than nested decorators
- Easier to serialize (for API)
- More maintainable

**When to Use Decorator**:
- Adding behavior, not just data
- Need runtime composition of features
- Multiple layers of wrapping needed

---

### 3. **Atomic Inventory Operations**

```java
public synchronized void reserveAndDeduct(Map<Ingredient, Integer> recipe) 
        throws OutOfStockException {
    // Check all ingredients first
    for (Entry<Ingredient, Integer> entry : recipe.entrySet()) {
        if (stock.get(entry.getKey()) < entry.getValue()) {
            throw new OutOfStockException(entry.getKey());
        }
    }
    
    // Then deduct all (atomic)
    for (Entry<Ingredient, Integer> entry : recipe.entrySet()) {
        stock.merge(entry.getKey(), -entry.getValue(), Integer::sum);
    }
}
```

**Why Atomic?**
- Prevents partial deductions
- Thread-safe
- All-or-nothing guarantee

---

### 4. **Transaction History**

```java
private final List<Transaction> transactionHistory;

// Record every transaction
Transaction transaction = new Transaction(...);
transactionHistory.add(transaction);
```

**Benefits**:
- Audit trail
- Sales tracking
- Revenue calculation
- Debugging

---

### 5. **Low Stock Alerts**

```java
public boolean isLowStock(Ingredient ingredient) {
    int current = stock.getOrDefault(ingredient, 0);
    int minimum = minLevels.getOrDefault(ingredient, 0);
    return current < minimum;
}
```

**Practical Feature**:
- Alerts when refill needed
- Prevents running out
- Better user experience

---

## Usage Example

```java
// Initialize
Inventory inventory = new Inventory();
PaymentProcessor paymentProcessor = new PaymentProcessor();
CoffeeMachine machine = new CoffeeMachine(inventory, paymentProcessor);

// Stock up
inventory.addStock(Ingredient.COFFEE_BEANS, 100);
inventory.addStock(Ingredient.WATER, 500);
inventory.addStock(Ingredient.MILK, 300);

// Make coffee
try {
    // 1. Select coffee with toppings
    machine.selectCoffee(
        CoffeeType.LATTE, 
        Arrays.asList(Topping.EXTRA_SUGAR, Topping.CARAMEL_SYRUP)
    );
    
    // 2. Insert money
    machine.insertMoney(300, PaymentMethod.CASH);
    
    // 3. Dispense
    Transaction transaction = machine.dispense();
    System.out.println("Success! " + transaction);
    
} catch (InsufficientFundsException e) {
    System.out.println("Not enough money: " + e.getMessage());
} catch (OutOfStockException e) {
    System.out.println("Out of stock: " + e.getMessage());
} catch (CoffeeMachineException e) {
    System.out.println("Error: " + e.getMessage());
}
```

---

## Running the Demo

```bash
cd ~/Downloads/LLD
javac coffeemachine/**/*.java coffeemachine/*.java
java coffeemachine.CoffeeMachineDemo
```

---

## Advantages of This Design

### ✅ **Simplicity**
- 50% less code than original
- No unnecessary patterns
- Easy to understand in 5 minutes

### ✅ **Maintainability**
- Clear structure
- Easy to debug
- Easy to modify

### ✅ **Testability**
- Dependency injection
- No singletons
- Easy to mock

### ✅ **Production Ready**
- Proper error handling
- Transaction logging
- Thread safety
- Low stock alerts

### ✅ **Extensibility**
- Easy to add new coffee types (just add enum)
- Easy to add new toppings (just add enum)
- Easy to add new payment methods (Strategy pattern if needed)

---

## When to Add Design Patterns

### **Add Factory Pattern When**:
- Coffee creation becomes complex
- Need different creation strategies
- Multiple coffee families

```java
public interface CoffeeFactory {
    Coffee createCoffee(CoffeeType type, List<Topping> toppings);
}

public class StandardCoffeeFactory implements CoffeeFactory { }
public class PremiumCoffeeFactory implements CoffeeFactory { }
```

### **Add Strategy Pattern When**:
- Multiple payment methods with different logic
- Need to swap algorithms at runtime

```java
public interface PaymentStrategy {
    int processPayment(int amount);
}

public class CashPayment implements PaymentStrategy { }
public class CardPayment implements PaymentStrategy { }
```

### **Add State Pattern When**:
- 10+ states
- Complex state transitions
- State-specific behavior is substantial

---

## Interview Talking Points

### **Q: Why no design patterns?**

**A**: "I used patterns where they add value:
- **Dependency Injection**: For testability
- **Enum for State**: Simple and clear
- **Exception Hierarchy**: Proper error handling

I avoided patterns that add complexity without benefit:
- State Pattern: Overkill for 4 states
- Decorator: List of toppings is simpler
- Template Method: Not needed here
- Factory: Direct instantiation is clearer

**The key is pragmatism over dogmatism.**"

### **Q: How would you extend this?**

**A**: "Very easy:

**Add new coffee**:
```java
// Just add to enum
FLAT_WHITE("Flat White", 210)
```

**Add new topping**:
```java
// Just add to enum
VANILLA_SYRUP("Vanilla", 20, Ingredient.VANILLA, 10)
```

**Add payment method**:
```java
// Add to enum or use Strategy pattern if logic differs
```

No need to modify existing code - Open/Closed Principle!"

---

## Comparison with Original

### **Original Design (Pattern-Heavy)**
```
✅ Great for learning patterns
✅ Shows OOP knowledge
❌ Over-engineered
❌ Hard to maintain
❌ Missing production features
```

### **This Design (Clean & Practical)**
```
✅ Simple and clear
✅ Production-ready
✅ Easy to test
✅ Easy to extend
✅ Proper error handling
✅ Transaction logging
```

---

## Summary

This clean design demonstrates:

✅ **Pragmatic engineering** - Use patterns when needed, not always  
✅ **SOLID principles** - Without over-engineering  
✅ **Production readiness** - Error handling, logging, thread safety  
✅ **Simplicity** - 50% less code, 100% clearer  
✅ **Extensibility** - Easy to add features  
✅ **Testability** - Dependency injection, no singletons  

**Perfect for showing you understand WHEN to use patterns, not just HOW!** 🚀

---

## Interview Strategy

**Show Both Versions**:
1. "Here's the original with 4 patterns - great for learning"
2. "Here's my clean version - better for production"
3. "In interviews, I'd discuss trade-offs and choose based on requirements"

**This shows maturity and real-world experience!** 🎯
