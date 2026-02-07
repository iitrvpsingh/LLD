package coffeemachine.service;

import coffeemachine.enums.PaymentMethod;
import coffeemachine.exception.InsufficientFundsException;

/**
 * Handles payment processing
 * Strategy pattern can be applied here for different payment methods
 */
public class PaymentProcessor {
    
    /**
     * Process payment
     * @return change amount
     */
    public int processPayment(int amountRequired, int amountProvided, PaymentMethod method) 
            throws InsufficientFundsException {
        
        if (amountProvided < amountRequired) {
            throw new InsufficientFundsException(amountRequired, amountProvided);
        }

        // Simulate payment processing based on method
        switch (method) {
            case CASH:
                System.out.println("💵 Processing cash payment...");
                break;
            case CARD:
                System.out.println("💳 Processing card payment...");
                // In production: integrate with payment gateway
                break;
            case MOBILE_PAYMENT:
                System.out.println("📱 Processing mobile payment...");
                break;
            case CONTACTLESS:
                System.out.println("📡 Processing contactless payment...");
                break;
        }

        int change = amountProvided - amountRequired;
        System.out.println("✓ Payment successful. Amount: " + amountRequired);
        
        return change;
    }

    /**
     * Refund payment
     */
    public void refund(int amount, PaymentMethod method) {
        System.out.println("💰 Refunding " + amount + " via " + method);
    }

    /**
     * Dispense change
     */
    public void dispenseChange(int change) {
        if (change > 0) {
            System.out.println("💰 Dispensing change: " + change);
        }
    }
}
