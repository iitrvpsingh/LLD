package carrentalsystem.strategy;

import carrentalsystem.exception.PaymentFailedException;

import java.util.UUID;

/**
 * Credit Card payment implementation
 */
public class CreditCardPayment implements PaymentStrategy {
    
    @Override
    public String processPayment(double amount, String customerId) throws PaymentFailedException {
        // Simulate credit card processing
        System.out.println("💳 Processing credit card payment: $" + amount);
        
        // In production: integrate with payment gateway (Stripe, PayPal, etc.)
        // - Validate card details
        // - Check fraud
        // - Process transaction
        
        if (amount <= 0) {
            throw new PaymentFailedException("Invalid payment amount: " + amount);
        }
        
        // Simulate success
        String transactionId = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("✓ Payment successful. Transaction ID: " + transactionId);
        
        return transactionId;
    }
    
    @Override
    public String refund(String transactionId, double amount) throws PaymentFailedException {
        System.out.println("💰 Processing credit card refund: $" + amount + " for transaction " + transactionId);
        
        // In production: process refund through payment gateway
        
        String refundId = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("✓ Refund successful. Refund ID: " + refundId);
        
        return refundId;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
}
