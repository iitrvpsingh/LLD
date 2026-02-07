package coffeemachine_patterns.model;

import coffeemachine_patterns.decorator.Coffee;
import coffeemachine_patterns.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transaction record (immutable)
 */
public class Transaction {
    private final String transactionId;
    private final String coffeeDescription;
    private final double amountPaid;
    private final double change;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime timestamp;
    private final boolean successful;

    public Transaction(Coffee coffee, double amountPaid, double change, 
                      PaymentMethod paymentMethod, boolean successful) {
        this.transactionId = UUID.randomUUID().toString();
        this.coffeeDescription = coffee.getDescription();
        this.amountPaid = amountPaid;
        this.change = change;
        this.paymentMethod = paymentMethod;
        this.timestamp = LocalDateTime.now();
        this.successful = successful;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId.substring(0, 8) + "...'" +
                ", coffee=" + coffeeDescription +
                ", paid=" + amountPaid +
                ", change=" + change +
                ", method=" + paymentMethod +
                ", successful=" + successful +
                ", time=" + timestamp +
                '}';
    }
}
