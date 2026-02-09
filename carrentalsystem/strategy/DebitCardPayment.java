package carrentalsystem.strategy;

import carrentalsystem.exception.PaymentFailedException;

import java.util.UUID;

/**
 * Debit Card payment implementation
 */
public class DebitCardPayment implements PaymentStrategy {
    
    @Override
    public String processPayment(double amount, String customerId) throws PaymentFailedException {
        System.out.println("💳 Processing debit card payment: $" + amount);
        
        if (amount <= 0) {
            throw new PaymentFailedException("Invalid payment amount: " + amount);
        }
        
        String transactionId = "DB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("✓ Payment successful. Transaction ID: " + transactionId);
        
        return transactionId;
    }
    
    @Override
    public String refund(String transactionId, double amount) throws PaymentFailedException {
        System.out.println("💰 Processing debit card refund: $" + amount);
        
        String refundId = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("✓ Refund successful. Refund ID: " + refundId);
        
        return refundId;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Debit Card";
    }
}
