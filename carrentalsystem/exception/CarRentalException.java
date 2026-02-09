package carrentalsystem.exception;

/**
 * Base exception for car rental system
 */
public class CarRentalException extends Exception {
    public CarRentalException(String message) {
        super(message);
    }

    public CarRentalException(String message, Throwable cause) {
        super(message, cause);
    }
}
