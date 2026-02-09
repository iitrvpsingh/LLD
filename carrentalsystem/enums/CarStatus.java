package carrentalsystem.enums;

/**
 * Status of a car in the fleet
 */
public enum CarStatus {
    AVAILABLE,      // Available for rental
    RENTED,         // Currently rented out
    MAINTENANCE,    // Under maintenance
    RETIRED         // Removed from fleet
}
