package carrentalsystem.enums;

/**
 * Status of a reservation
 */
public enum ReservationStatus {
    PENDING_PAYMENT,    // Created but not paid
    CONFIRMED,          // Paid and confirmed
    IN_PROGRESS,        // Car picked up
    COMPLETED,          // Car returned
    CANCELLED           // Cancelled by customer or system
}
