package carrentalsystem.exception;

public class ReservationNotFoundException extends CarRentalException {
    public ReservationNotFoundException(String reservationId) {
        super("Reservation not found: " + reservationId);
    }
}
