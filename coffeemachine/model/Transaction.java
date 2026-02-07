package coffeemachine.model;

import coffeemachine.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a transaction record
 * Immutable for audit trail
 */
public class Transaction {
    private final String transactionId;
    private final Coffee coffee;
    private final int amountPaid;
    private final int change;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime timestamp;
    private final boolean successful;

    public Transaction(Coffee coffee, int amountPaid, int change, 
                      PaymentMethod paymentMethod, boolean successful) {
        this.transactionId = UUID.randomUUID().toString();
        this.coffee = coffee;
        this.amountPaid = amountPaid;
        this.change = change;
        this.paymentMethod = paymentMethod;
        this.timestamp = LocalDateTime.now();
        this.successful = successful;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Coffee getCoffee() {
        return coffee;
    }

    public int getAmountPaid() {
        return amountPaid;
    }

    public int getChange() {
        return change;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId.substring(0, 8) + "...'" +
                ", coffee=" + coffee.getDescription() +
                ", paid=" + amountPaid +
                ", change=" + change +
                ", method=" + paymentMethod +
                ", successful=" + successful +
                ", time=" + timestamp +
                '}';
    }
}
