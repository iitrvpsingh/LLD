package carrentalsystem.strategy;

import carrentalsystem.exception.PaymentFailedException;

/**
 * Strategy Pattern for payment processing
 * Allows different payment methods to be plugged in
 */
public interface PaymentStrategy {
    /**
     * Process payment
     * @param amount Amount to charge
     * @param customerId Customer identifier
     * @return Transaction ID if successful
     * @throws PaymentFailedException if payment fails
     */
    String processPayment(double amount, String customerId) throws PaymentFailedException;
    
    /**
     * Refund payment
     * @param transactionId Original transaction ID
     * @param amount Amount to refund
     * @return Refund transaction ID
     * @throws PaymentFailedException if refund fails
     */
    String refund(String transactionId, double amount) throws PaymentFailedException;
    
    /**
     * Get payment method name
     */
    String getPaymentMethodName();
}
