package carrentalsystem.strategy;

import carrentalsystem.exception.PaymentFailedException;

import java.util.UUID;

/**
 * Cash payment implementation
 */
public class CashPayment implements PaymentStrategy {
    
    @Override
    public String processPayment(double amount, String customerId) throws PaymentFailedException {
        System.out.println("💵 Processing cash payment: $" + amount);
        
        if (amount <= 0) {
            throw new PaymentFailedException("Invalid payment amount: " + amount);
        }
        
        String transactionId = "CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("✓ Cash payment received. Receipt ID: " + transactionId);
        
        return transactionId;
    }
    
    @Override
    public String refund(String transactionId, double amount) throws PaymentFailedException {
        System.out.println("💰 Processing cash refund: $" + amount);
        
        String refundId = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("✓ Cash refund completed. Receipt ID: " + refundId);
        
        return refundId;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Cash";
    }
}
