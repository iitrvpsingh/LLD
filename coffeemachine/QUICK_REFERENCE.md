# Coffee Vending Machine - Quick Reference Card

## 30-Second Pitch

"I designed a **clean, production-ready coffee vending machine** that handles coffee selection, payments, inventory, and transactions with proper error handling and thread safety. I prioritized **simplicity over pattern showcase** - using patterns only where they add value."

---

## Architecture (1 minute)

```
CoffeeMachine (Controller)
    ├── Inventory (Stock Management)
    ├── PaymentProcessor (Payments)
    ├── Coffee (Order Model)
    └── Transaction (Audit Trail)

State Flow: IDLE → SELECTED → PAID → DISPENSING → IDLE
```

---

## Key Design Decisions

| Decision | Reason |
|----------|--------|
| **Enum for State** | Simple, clear - State Pattern is overkill for 4 states |
| **List for Toppings** | Data, not behavior - Decorator is overkill |
| **Direct Instantiation** | Simple creation - Factory is overkill |
| **Dependency Injection** | Testability and flexibility |
| **Synchronized Methods** | Thread safety for inventory |
| **Custom Exceptions** | Structured error handling |

---

## SOLID Principles (Quick)

1. **SRP**: Each class has one job (Machine, Inventory, Payment, Coffee)
2. **OCP**: Add coffee/toppings via enums - no code changes
3. **LSP**: Substitutable components (payment strategies)
4. **ISP**: Minimal interfaces - no fat interfaces
5. **DIP**: Inject dependencies, don't create them

---

## Patterns Used vs Avoided

### ✅ Used
- **Dependency Injection**: Testability
- **Value Object**: Immutable Transaction

### ❌ Avoided (And Why)
- **State Pattern**: Overkill for 4 simple states
- **Decorator**: Toppings are data, not behavior
- **Factory**: Creation is simple
- **Template Method**: Preparation is straightforward
- **Singleton**: Anti-pattern (tight coupling)

---

## Thread Safety

```java
// Atomic check-and-deduct
public synchronized void reserveAndDeduct(Map<Ingredient, Integer> recipe) {
    // Check all ingredients first
    // Then deduct all (atomic)
}
```

**Why**: Prevents race conditions in concurrent orders

---

## Error Handling

```
CoffeeMachineException
    ├── InsufficientFundsException
    ├── OutOfStockException
    └── InvalidStateException
```

**Auto-refund on errors** - graceful degradation

---

## Extensibility Examples

### Add Coffee Type (2 min)
```java
FLAT_WHITE("Flat White", 210)  // Just add to enum!
```

### Add Topping (1 min)
```java
VANILLA("Vanilla", 20, Ingredient.VANILLA, 10)  // Just add to enum!
```

---

## Common Interview Questions

### Q: Why no design patterns?

**A**: "I used patterns where they add value (DI). I avoided patterns that add complexity without benefit (State, Decorator, Factory). This shows I understand WHEN to use patterns, not just HOW."

---

### Q: How would you make this distributed?

**A**: 
1. Database for state (not in-memory)
2. Distributed locking (Redis)
3. Message queue (Kafka)
4. REST API
5. Monitoring (Prometheus)

---

### Q: How would you test this?

**A**: "Dependency injection makes it trivial:
```java
Inventory mockInventory = mock(Inventory.class);
CoffeeMachine machine = new CoffeeMachine(mockInventory, mockProcessor);
```

---

### Q: What would you improve?

**A**:
1. Persistent storage (database)
2. Monitoring & alerts
3. Configuration externalization
4. Security (encryption, audit)
5. UI/API layer
6. Advanced features (loyalty, promotions)

---

## Comparison: Original vs Clean

| Metric | Original | Clean | Improvement |
|--------|----------|-------|-------------|
| Lines | ~500 | ~300 | **40% less** |
| Classes | 15+ | 10 | **33% fewer** |
| Patterns | 4 | 1 | **Simpler** |
| Complexity | High | Low | **Much better** |
| Production Ready | ❌ | ✅ | **Much better** |

---

## Key Talking Points

1. **Pragmatic Engineering** - Simplicity over complexity
2. **SOLID Principles** - Applied thoughtfully
3. **Production Ready** - Error handling, thread safety, logging
4. **Pattern Knowledge** - Know when NOT to use them
5. **Testable** - Dependency injection

---

## Files Structure

```
coffeemachine/
├── enums/           (CoffeeType, Ingredient, Topping, PaymentMethod)
├── model/           (Coffee, Transaction)
├── service/         (Inventory, PaymentProcessor)
├── exception/       (Custom exception hierarchy)
├── CoffeeMachine.java
└── CoffeeMachineDemo.java
```

---

## Running Demo

```bash
cd ~/Downloads/LLD
javac coffeemachine/**/*.java coffeemachine/*.java
java coffeemachine.CoffeeMachineDemo
```

---

## Interview Strategy

1. **Show Clean Version First**: "Here's my production design"
2. **Discuss Original**: "Here's the pattern-heavy version"
3. **Compare Trade-offs**: "I'd choose based on requirements"
4. **Show Maturity**: "Pragmatism over dogmatism"

---

## Time to Implement

**Total**: ~3.5 hours (Medium difficulty)

---

## Final Checklist

- ✅ Can explain architecture in 2 minutes
- ✅ Can discuss each SOLID principle
- ✅ Can explain why avoided patterns
- ✅ Can discuss thread safety
- ✅ Can show extensibility examples
- ✅ Can compare with original
- ✅ Can discuss production improvements
- ✅ Can explain testing strategy

---

## Remember

**This design shows you're a senior engineer who:**
- ✅ Values simplicity and maintainability
- ✅ Knows when to use patterns (and when not to)
- ✅ Thinks about production concerns
- ✅ Prioritizes pragmatism over dogmatism

**Perfect for interviews!** 🚀
