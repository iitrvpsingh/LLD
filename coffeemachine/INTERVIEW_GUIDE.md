# Coffee Vending Machine - Interview Guide

## Quick Overview (30 seconds)

"I designed a **clean, production-ready coffee vending machine** that prioritizes simplicity and maintainability over pattern showcase. It handles coffee selection, payments, inventory management, and transaction logging with proper error handling and thread safety."

---

## Architecture Explanation (2 minutes)

### **Core Components**

```
CoffeeMachine (Main Controller)
    ├── Inventory (Ingredient Management)
    ├── PaymentProcessor (Payment Handling)
    ├── Coffee (Order Model)
    └── Transaction (Audit Trail)
```

### **State Flow**

```
IDLE → SELECTED → PAID → DISPENSING → IDLE
  ↓       ↓         ↓         ↓
Select  Insert   Dispense  Reset
Coffee  Money    Coffee
```

### **Key Design Decisions**

1. **Simple State Management**: Enum instead of State Pattern
   - Why? Only 4 states with linear flow
   - State Pattern would be 5+ classes for same functionality

2. **Dependency Injection**: Services injected, not created
   - Why? Testability and flexibility
   - Can easily mock for unit tests

3. **Atomic Operations**: Check-then-deduct is synchronized
   - Why? Prevents race conditions in concurrent orders
   - All-or-nothing guarantee

---

## SOLID Principles Applied

### **1. Single Responsibility Principle** ✅

```java
// Each class has ONE job
CoffeeMachine    → Coordinates order flow
Inventory        → Manages ingredients
PaymentProcessor → Handles payments
Coffee           → Represents order
Transaction      → Records transaction
```

**Interview Answer**: "Each component has a single, well-defined responsibility. For example, `Inventory` only manages stock levels, not payments or coffee preparation."

---

### **2. Open/Closed Principle** ✅

```java
// Adding new coffee type - NO modification needed
public enum CoffeeType {
    ESPRESSO("Espresso", 150),
    LATTE("Latte", 220),
    NEW_COFFEE("New Coffee", 300)  // Just add here!
}

// Recipe defined in Coffee class
private Map<Ingredient, Integer> getBaseRecipe() {
    switch (type) {
        case NEW_COFFEE:
            return Map.of(Ingredient.COFFEE_BEANS, 10);
        // ...
    }
}
```

**Interview Answer**: "The system is open for extension but closed for modification. I can add new coffee types by just adding enum values and recipes - no existing code changes needed."

---

### **3. Liskov Substitution Principle** ✅

```java
// Not heavily used here, but if we had payment strategies:
public interface PaymentStrategy {
    int processPayment(int amount);
}

// Any implementation can substitute the interface
public class CashPayment implements PaymentStrategy { }
public class CardPayment implements PaymentStrategy { }
```

**Interview Answer**: "While not heavily demonstrated here, the design allows for substitutable components. For example, if we extract payment strategies, any implementation can replace another without breaking the system."

---

### **4. Interface Segregation Principle** ✅

```java
// No fat interfaces - each component exposes only what's needed
public class CoffeeMachine {
    // Only essential public methods
    public void selectCoffee(...)
    public void insertMoney(...)
    public Transaction dispense()
    public void cancel()
    
    // Internal methods are private
    private void prepareCoffee(...)
    private void refundAndReset()
}
```

**Interview Answer**: "Each component exposes a minimal, focused interface. Clients only see what they need to interact with."

---

### **5. Dependency Inversion Principle** ✅

```java
// High-level module depends on abstractions (injected services)
public CoffeeMachine(Inventory inventory, PaymentProcessor paymentProcessor) {
    this.inventory = inventory;           // Injected
    this.paymentProcessor = paymentProcessor;  // Injected
}

// Benefits:
// - Can inject mocks for testing
// - Can swap implementations
// - No tight coupling to concrete classes
```

**Interview Answer**: "The machine depends on injected services, not concrete implementations. This makes it testable and flexible - I can inject mocks for unit tests or swap implementations without changing the machine."

---

## Design Pattern Discussion

### **Patterns I Used**

#### **1. Dependency Injection** ✅

```java
// Constructor injection
public CoffeeMachine(Inventory inventory, PaymentProcessor paymentProcessor) {
    this.inventory = inventory;
    this.paymentProcessor = paymentProcessor;
}
```

**Why**: Testability, flexibility, loose coupling

---

#### **2. Immutable Value Object (Transaction)** ✅

```java
public class Transaction {
    private final String transactionId;
    private final Coffee coffee;
    private final int amountPaid;
    // No setters - immutable for audit trail
}
```

**Why**: Audit trail integrity, thread safety

---

### **Patterns I Avoided (And Why)**

#### **1. State Pattern** ❌

**Original Design**:
```java
// 5 classes for 4 states
interface VendingMachineState { }
class ReadyState implements VendingMachineState { }
class SelectingState implements VendingMachineState { }
class PaidState implements VendingMachineState { }
class OutOfIngredientState implements VendingMachineState { }
```

**My Design**:
```java
// Simple enum
private enum State {
    IDLE, SELECTED, PAID, DISPENSING
}
```

**Interview Answer**: "State Pattern is powerful but overkill here. We have only 4 states with linear flow. An enum is simpler, clearer, and easier to maintain. I'd use State Pattern if we had 10+ states or complex state-specific behavior."

**When I'd Use State Pattern**:
- 10+ states
- Complex state transitions (non-linear)
- Substantial state-specific behavior
- State behavior changes frequently

---

#### **2. Decorator Pattern** ❌

**Original Design**:
```java
// Nested decorators
Coffee coffee = new Espresso();
coffee = new ExtraSugarDecorator(coffee);
coffee = new CaramelSyrupDecorator(coffee);
```

**My Design**:
```java
// Simple list
Coffee coffee = new Coffee(
    CoffeeType.ESPRESSO,
    Arrays.asList(Topping.EXTRA_SUGAR, Topping.CARAMEL_SYRUP)
);
```

**Interview Answer**: "Decorator is great for adding behavior, but toppings are just data. A list is simpler, easier to serialize (for APIs), and more maintainable. I'd use Decorator if toppings had complex behavior or needed runtime composition of features."

**When I'd Use Decorator**:
- Adding behavior, not just data
- Need runtime composition
- Multiple layers of wrapping
- Example: Logging, caching, compression

---

#### **3. Factory Pattern** ❌

**Original Design**:
```java
public class CoffeeFactory {
    public Coffee createCoffee(CoffeeType type) { }
}
```

**My Design**:
```java
// Direct instantiation
Coffee coffee = new Coffee(type, toppings);
```

**Interview Answer**: "Factory is useful when creation is complex, but here it's simple. Direct instantiation is clearer. I'd add Factory if we had multiple coffee families or complex creation logic."

**When I'd Use Factory**:
- Complex creation logic
- Multiple product families
- Creation depends on configuration
- Example: DatabaseConnectionFactory

---

#### **4. Template Method Pattern** ❌

**Original Design**:
```java
public abstract class Coffee {
    public final void prepare() {
        grindBeans();
        heatWater();
        brew();
        addCondiments();  // Hook method
    }
    protected abstract void addCondiments();
}
```

**My Design**:
```java
// Simple method
private void prepareCoffee(Coffee coffee) {
    System.out.println("Grinding beans");
    System.out.println("Heating water");
    System.out.println("Brewing");
    // Add toppings
}
```

**Interview Answer**: "Template Method is great for algorithms with varying steps, but here preparation is straightforward. A simple method is clearer. I'd use Template Method if we had multiple preparation algorithms with common structure."

---

## Thread Safety

### **Critical Section: Inventory**

```java
public synchronized void reserveAndDeduct(Map<Ingredient, Integer> recipe) 
        throws OutOfStockException {
    // Step 1: Check all ingredients (atomic)
    for (Entry<Ingredient, Integer> entry : recipe.entrySet()) {
        if (stock.get(entry.getKey()) < entry.getValue()) {
            throw new OutOfStockException(entry.getKey());
        }
    }
    
    // Step 2: Deduct all (atomic)
    for (Entry<Ingredient, Integer> entry : recipe.entrySet()) {
        stock.merge(entry.getKey(), -entry.getValue(), Integer::sum);
    }
}
```

**Interview Answer**: "The critical section is inventory management. I use `synchronized` to ensure check-and-deduct is atomic. This prevents race conditions where two concurrent orders could deplete stock below zero."

**Alternative Approaches**:
- `ReadWriteLock`: If reads >> writes
- `AtomicInteger`: For simple counters
- Database transactions: In distributed systems

---

## Error Handling

### **Exception Hierarchy**

```
CoffeeMachineException (base)
    ├── InsufficientFundsException
    ├── OutOfStockException
    └── InvalidStateException
```

### **Graceful Degradation**

```java
try {
    machine.dispense();
} catch (OutOfStockException e) {
    // Refund automatically
    paymentProcessor.refund(moneyInserted, paymentMethod);
    reset();
}
```

**Interview Answer**: "I use a custom exception hierarchy for clear error handling. When errors occur, the system automatically refunds and resets - no manual cleanup needed. This ensures good user experience even during failures."

---

## Extensibility Examples

### **1. Add New Coffee Type**

```java
// Just add to enum - that's it!
public enum CoffeeType {
    FLAT_WHITE("Flat White", 210)
}

// Add recipe in Coffee class
case FLAT_WHITE:
    return Map.of(
        Ingredient.COFFEE_BEANS, 8,
        Ingredient.WATER, 30,
        Ingredient.MILK, 120
    );
```

**Time**: 2 minutes  
**Files Changed**: 1  
**Lines Added**: ~5

---

### **2. Add New Topping**

```java
// Just add to enum
public enum Topping {
    VANILLA_SYRUP("Vanilla", 20, Ingredient.VANILLA, 10)
}
```

**Time**: 1 minute  
**Files Changed**: 1  
**Lines Added**: 1

---

### **3. Add Payment Strategy**

```java
// Extract interface
public interface PaymentStrategy {
    int processPayment(int required, int provided) throws InsufficientFundsException;
}

// Implement strategies
public class CashPayment implements PaymentStrategy { }
public class CardPayment implements PaymentStrategy { }
public class MobilePayment implements PaymentStrategy { }

// Inject into machine
public CoffeeMachine(Inventory inventory, PaymentStrategy paymentStrategy) { }
```

**Time**: 15 minutes  
**Files Changed**: 3  
**Lines Added**: ~50

---

## Comparison: Original vs Clean

| Aspect | Original | Clean | Winner |
|--------|----------|-------|--------|
| **Lines of Code** | ~500 | ~300 | Clean |
| **Classes** | 15+ | 10 | Clean |
| **Patterns Used** | 4 | 1 (DI) | Clean |
| **Complexity** | High | Low | Clean |
| **Maintainability** | Medium | High | Clean |
| **Testability** | Medium | High | Clean |
| **Production Ready** | No | Yes | Clean |
| **Learning Value** | High | Medium | Original |
| **Interview Impact** | Good | Better | Clean |

---

## Common Interview Questions

### **Q1: Why no design patterns?**

**A**: "I used patterns where they add value - Dependency Injection for testability, immutable value objects for audit trails. I avoided patterns that add complexity without benefit. The key is pragmatism: use patterns to solve problems, not just to use patterns.

For example, State Pattern would be 5 classes for 4 simple states. An enum is clearer and easier to maintain. This shows I understand WHEN to use patterns, not just HOW."

---

### **Q2: How would you make this distributed?**

**A**: "Great question! I'd make these changes:

1. **Replace in-memory state with database**
   - Inventory → Database table
   - Transactions → Database table

2. **Add distributed locking** (Redis/Zookeeper)
   ```java
   DistributedLock lock = new RedisLock();
   lock.acquire("inventory:" + ingredient);
   try {
       // Check and deduct
   } finally {
       lock.release();
   }
   ```

3. **Add message queue** (Kafka/RabbitMQ)
   - Async order processing
   - Event sourcing for audit trail

4. **Add API layer** (REST/gRPC)
   - Expose endpoints
   - Handle authentication

5. **Add monitoring** (Prometheus/Grafana)
   - Track stock levels
   - Alert on failures"

---

### **Q3: How would you test this?**

**A**: "Multiple levels:

**Unit Tests**:
```java
@Test
public void testInsufficientFunds() {
    Inventory inventory = new Inventory();
    PaymentProcessor processor = new PaymentProcessor();
    CoffeeMachine machine = new CoffeeMachine(inventory, processor);
    
    machine.selectCoffee(CoffeeType.ESPRESSO, Collections.emptyList());
    machine.insertMoney(100, PaymentMethod.CASH);
    
    assertThrows(InvalidStateException.class, () -> machine.dispense());
}
```

**Integration Tests**:
- Test full order flow
- Test concurrent orders
- Test out-of-stock scenarios

**Mock Dependencies**:
```java
Inventory mockInventory = mock(Inventory.class);
when(mockInventory.hasIngredients(any())).thenReturn(true);
```

**Benefits of DI**: Easy to inject mocks!"

---

### **Q4: What would you improve?**

**A**: "For production, I'd add:

1. **Persistent Storage**
   - Database for transactions
   - Inventory tracking

2. **Monitoring & Alerts**
   - Low stock alerts
   - Failure notifications
   - Revenue tracking

3. **Configuration**
   - Externalized prices
   - Recipe configuration
   - Min stock levels

4. **Security**
   - Payment encryption
   - Audit logging
   - Access control

5. **UI/API**
   - REST endpoints
   - Admin dashboard
   - Mobile app integration

6. **Advanced Features**
   - Loyalty program
   - Promotions/discounts
   - Scheduled maintenance mode"

---

### **Q5: How does this handle concurrency?**

**A**: "Two levels of protection:

**1. Inventory Level** (synchronized):
```java
public synchronized void reserveAndDeduct(...) {
    // Atomic check-and-deduct
}
```

**2. Machine Level** (isolated instances):
- Each order gets its own machine instance
- No shared mutable state between orders

**Demo shows**: 10 concurrent orders processed safely with proper stock management."

---

## Key Talking Points

### **1. Pragmatic Engineering** 🎯
"I prioritize simplicity and maintainability over pattern showcase. This shows real-world experience."

### **2. SOLID Principles** ✅
"Every principle is applied thoughtfully, not just for the sake of it."

### **3. Production Ready** 🚀
"Error handling, thread safety, transaction logging, low stock alerts - this could run in production."

### **4. Extensible** 📈
"Adding features is easy - demonstrated with concrete examples."

### **5. Testable** 🧪
"Dependency injection makes unit testing trivial."

### **6. Pattern Knowledge** 🎓
"I can explain why I didn't use certain patterns - shows deep understanding."

---

## Interview Strategy

### **Show Both Versions**

1. **Original**: "This uses 4 patterns - State, Factory, Decorator, Template Method. Great for learning."

2. **Clean**: "This is my production version - simpler, clearer, more maintainable."

3. **Discussion**: "In interviews, I'd discuss trade-offs and choose based on requirements."

### **This Shows**:
- ✅ Pattern knowledge (you know them)
- ✅ Judgment (you know when to use them)
- ✅ Experience (you've seen over-engineering)
- ✅ Maturity (pragmatism over dogmatism)

---

## Time Estimates for Implementation

| Component | Time | Difficulty |
|-----------|------|------------|
| Basic structure | 30 min | Easy |
| Coffee model | 20 min | Easy |
| Inventory | 30 min | Medium |
| Payment | 20 min | Easy |
| Main machine | 45 min | Medium |
| Error handling | 30 min | Medium |
| Thread safety | 30 min | Medium |
| Demo | 30 min | Easy |
| **Total** | **~3.5 hours** | **Medium** |

---

## Final Checklist

Before interview, ensure you can:

- ✅ Explain architecture in 2 minutes
- ✅ Discuss each SOLID principle with examples
- ✅ Explain why you avoided certain patterns
- ✅ Discuss thread safety approach
- ✅ Show extensibility with concrete examples
- ✅ Compare with original design
- ✅ Discuss production improvements
- ✅ Explain testing strategy

---

## Summary

This design demonstrates:

✅ **Clean Code** - Simple, readable, maintainable  
✅ **SOLID Principles** - Applied thoughtfully  
✅ **Pattern Knowledge** - Know when to use and when not to  
✅ **Production Mindset** - Error handling, logging, thread safety  
✅ **Pragmatism** - Solve problems, don't showcase patterns  

**Perfect for showing you're a senior engineer who values simplicity and maintainability!** 🚀
