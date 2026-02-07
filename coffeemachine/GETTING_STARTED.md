# Getting Started - Clean Coffee Machine

## Quick Start (5 minutes)

### 1. Compile

```bash
cd ~/Downloads/LLD
javac coffeemachine/**/*.java coffeemachine/*.java
```

### 2. Run Demo

```bash
java coffeemachine.CoffeeMachineDemo
```

### 3. Expected Output

```
=== Coffee Vending Machine Demo ===

🔧 Setting up inventory...
=== Current Inventory ===
  COFFEE_BEANS: 100
  WATER: 500
  MILK: 300
  ...

--- SCENARIO 1: Buy Latte with Extra Sugar (Success) ---
☕ Selected: Latte with Extra Sugar, Caramel Syrup
💰 Price: 250
...
✅ Enjoy your Latte with Extra Sugar, Caramel Syrup!

--- SCENARIO 2: Insufficient Funds & Cancel ---
...

--- SCENARIO 3: Out of Stock ---
...

--- SCENARIO 4: Concurrent Orders (Thread Safety) ---
...

=== Demo Complete ===
```

---

## Understanding the Code (10 minutes)

### Step 1: Look at Enums First

Start with `enums/` folder to understand the domain:

```java
// Coffee types with prices
CoffeeType.LATTE → 220
CoffeeType.ESPRESSO → 150

// Toppings with prices
Topping.EXTRA_SUGAR → 10
Topping.CARAMEL_SYRUP → 20

// Ingredients
Ingredient.COFFEE_BEANS
Ingredient.MILK

// Payment methods
PaymentMethod.CASH
PaymentMethod.CARD
```

### Step 2: Understand Models

```java
// Coffee = Type + Toppings
Coffee coffee = new Coffee(
    CoffeeType.LATTE,
    Arrays.asList(Topping.EXTRA_SUGAR)
);

// Transaction = Audit record (immutable)
Transaction t = new Transaction(coffee, 300, 50, PaymentMethod.CASH, true);
```

### Step 3: Understand Services

```java
// Inventory = Stock management
Inventory inventory = new Inventory();
inventory.addStock(Ingredient.COFFEE_BEANS, 100);
inventory.reserveAndDeduct(recipe);  // Thread-safe

// PaymentProcessor = Payment handling
PaymentProcessor processor = new PaymentProcessor();
int change = processor.processPayment(required, provided, method);
```

### Step 4: Understand Main Machine

```java
// CoffeeMachine = Orchestrator
CoffeeMachine machine = new CoffeeMachine(inventory, processor);

// Flow: Select → Pay → Dispense
machine.selectCoffee(CoffeeType.LATTE, toppings);
machine.insertMoney(300, PaymentMethod.CASH);
Transaction t = machine.dispense();
```

---

## Basic Usage Example

```java
import coffeemachine.*;
import coffeemachine.enums.*;
import coffeemachine.service.*;
import java.util.*;

public class MyExample {
    public static void main(String[] args) {
        // 1. Setup
        Inventory inventory = new Inventory();
        PaymentProcessor processor = new PaymentProcessor();
        CoffeeMachine machine = new CoffeeMachine(inventory, processor);
        
        // 2. Stock up
        inventory.addStock(Ingredient.COFFEE_BEANS, 100);
        inventory.addStock(Ingredient.WATER, 500);
        inventory.addStock(Ingredient.MILK, 300);
        
        // 3. Make coffee
        try {
            // Select
            machine.selectCoffee(
                CoffeeType.LATTE,
                Arrays.asList(Topping.EXTRA_SUGAR)
            );
            
            // Pay
            machine.insertMoney(300, PaymentMethod.CASH);
            
            // Dispense
            Transaction t = machine.dispense();
            System.out.println("Success! " + t);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

---

## Exploring the Demo

The demo shows 4 scenarios:

### Scenario 1: Successful Purchase ✅
- Select Latte with toppings
- Insert sufficient money
- Dispense successfully
- Shows normal flow

### Scenario 2: Insufficient Funds ❌
- Select coffee
- Insert insufficient money
- Try to dispense (fails)
- Cancel and get refund
- Shows error handling

### Scenario 3: Out of Stock ❌
- Drain inventory
- Try to order (fails)
- Auto-refund
- Refill inventory
- Shows inventory management

### Scenario 4: Concurrent Orders 🔄
- 10 concurrent orders
- Some succeed, some fail (out of stock)
- Shows thread safety
- Shows atomic operations

---

## Key Files to Read

### For Interview Prep

1. **README.md** (15 min)
   - Overall design philosophy
   - Architecture overview
   - Key features

2. **INTERVIEW_GUIDE.md** (30 min)
   - Common questions & answers
   - SOLID principles explanation
   - Pattern discussion

3. **COMPARISON.md** (20 min)
   - Original vs Clean
   - Side-by-side comparison
   - When to use each approach

4. **QUICK_REFERENCE.md** (5 min)
   - Quick facts
   - Key talking points
   - Checklist

### For Understanding Code

1. **enums/** folder
   - Domain model
   - Business rules

2. **model/Coffee.java**
   - Core domain object
   - Recipe calculation

3. **service/Inventory.java**
   - Thread safety
   - Atomic operations

4. **CoffeeMachine.java**
   - Main orchestrator
   - State management
   - Error handling

5. **CoffeeMachineDemo.java**
   - Usage examples
   - Test scenarios

---

## Modifying the Code

### Add New Coffee Type

**File**: `enums/CoffeeType.java`

```java
FLAT_WHITE("Flat White", 210),
```

**File**: `model/Coffee.java`

```java
case FLAT_WHITE:
    return Map.of(
        Ingredient.COFFEE_BEANS, 8,
        Ingredient.WATER, 30,
        Ingredient.MILK, 120
    );
```

**Time**: 2 minutes

---

### Add New Topping

**File**: `enums/Topping.java`

```java
VANILLA_SYRUP("Vanilla Syrup", 20, Ingredient.VANILLA, 10),
```

**Time**: 1 minute

---

### Add New Ingredient

**File**: `enums/Ingredient.java`

```java
VANILLA,
```

**Time**: 30 seconds

---

## Testing Your Changes

### Manual Test

```bash
# Compile
javac coffeemachine/**/*.java coffeemachine/*.java

# Run
java coffeemachine.CoffeeMachineDemo
```

### Write Unit Test

```java
@Test
public void testNewCoffeeType() {
    Inventory inventory = new Inventory();
    inventory.addStock(Ingredient.COFFEE_BEANS, 100);
    inventory.addStock(Ingredient.WATER, 500);
    
    PaymentProcessor processor = new PaymentProcessor();
    CoffeeMachine machine = new CoffeeMachine(inventory, processor);
    
    machine.selectCoffee(CoffeeType.FLAT_WHITE, Collections.emptyList());
    machine.insertMoney(300, PaymentMethod.CASH);
    
    Transaction t = machine.dispense();
    assertEquals(CoffeeType.FLAT_WHITE, t.getCoffee().getType());
}
```

---

## Common Issues

### Issue 1: Compilation Error

```
Error: package coffeemachine does not exist
```

**Solution**: Make sure you're in the correct directory
```bash
cd ~/Downloads/LLD
```

### Issue 2: ClassNotFoundException

```
Error: Could not find or load main class coffeemachine.CoffeeMachineDemo
```

**Solution**: Compile first
```bash
javac coffeemachine/**/*.java coffeemachine/*.java
```

### Issue 3: OutOfStockException

```
OutOfStockException: Out of stock: MILK
```

**Solution**: Add more stock
```java
inventory.addStock(Ingredient.MILK, 500);
```

---

## Next Steps

### For Interview Prep

1. ✅ Read README.md (understand design)
2. ✅ Read INTERVIEW_GUIDE.md (prepare answers)
3. ✅ Read COMPARISON.md (understand trade-offs)
4. ✅ Run demo (see it in action)
5. ✅ Modify code (add coffee type)
6. ✅ Practice explaining (30-second pitch)

### For Learning

1. ✅ Compare with original design
2. ✅ Try adding features (promotions, loyalty)
3. ✅ Add unit tests
4. ✅ Add payment strategies
5. ✅ Add persistent storage
6. ✅ Add REST API

---

## Interview Preparation Checklist

- [ ] Can explain architecture in 2 minutes
- [ ] Can discuss SOLID principles with examples
- [ ] Can explain why avoided certain patterns
- [ ] Can discuss thread safety approach
- [ ] Can show extensibility (add coffee type)
- [ ] Can compare with original design
- [ ] Can discuss production improvements
- [ ] Can explain testing strategy
- [ ] Can answer "Why no patterns?"
- [ ] Can answer "How to make distributed?"

---

## Resources

### In This Folder

- `README.md` - Main documentation
- `INTERVIEW_GUIDE.md` - Interview Q&A
- `COMPARISON.md` - Original vs Clean
- `QUICK_REFERENCE.md` - Quick facts
- `GETTING_STARTED.md` - This file

### Code

- `CoffeeMachine.java` - Main class
- `CoffeeMachineDemo.java` - Demo scenarios
- `enums/` - Domain model
- `model/` - Core objects
- `service/` - Business logic
- `exception/` - Error handling

---

## Tips for Success

### 1. Start Simple
Don't try to understand everything at once. Start with:
1. Enums (domain model)
2. Coffee model
3. Basic flow (select → pay → dispense)

### 2. Run the Demo
See it in action before diving into code.

### 3. Compare with Original
Understanding why this is better helps in interviews.

### 4. Practice Explaining
The 30-second pitch is crucial:
> "Clean, production-ready coffee machine that prioritizes simplicity over pattern showcase. Uses patterns where they add value (DI), avoids where they don't (State, Decorator). Shows I understand WHEN to use patterns, not just HOW."

### 5. Be Ready to Discuss Trade-offs
- When would State Pattern be better?
- When would Decorator be better?
- What are the limitations of this design?

---

## Questions?

If you're stuck or have questions:

1. Read the relevant documentation file
2. Look at the demo code
3. Try running and modifying the code
4. Compare with the original design

---

## Summary

This is a **clean, production-ready coffee vending machine** that demonstrates:

✅ Pragmatic engineering  
✅ SOLID principles  
✅ Pattern knowledge (when to use and when not to)  
✅ Production concerns (error handling, thread safety)  
✅ Testability  
✅ Maintainability  

**Perfect for showing you're a senior engineer in interviews!** 🚀

---

## Good Luck! 🎯

You've got this! The key is showing you understand:
1. **WHEN** to use patterns (not just HOW)
2. **Pragmatism** over dogmatism
3. **Production** concerns (error handling, thread safety)
4. **Trade-offs** in design decisions

This design demonstrates all of that! 💪
