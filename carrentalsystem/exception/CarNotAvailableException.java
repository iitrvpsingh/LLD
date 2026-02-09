package carrentalsystem.exception;

import java.time.LocalDate;

public class CarNotAvailableException extends CarRentalException {
    public CarNotAvailableException(String licensePlate, LocalDate startDate, LocalDate endDate) {
        super("Car " + licensePlate + " is not available from " + startDate + " to " + endDate);
    }
}
