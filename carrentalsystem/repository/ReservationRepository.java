package carrentalsystem.repository;

import carrentalsystem.enums.ReservationStatus;
import carrentalsystem.model.Car;
import carrentalsystem.model.Customer;
import carrentalsystem.model.Reservation;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for Reservation data access
 * Thread-safe using ConcurrentHashMap
 */
public class ReservationRepository {
    private final Map<String, Reservation> reservations;  // reservationId -> Reservation

    public ReservationRepository() {
        this.reservations = new ConcurrentHashMap<>();
    }

    public void save(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
    }

    public Optional<Reservation> findById(String reservationId) {
        return Optional.ofNullable(reservations.get(reservationId));
    }

    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    public List<Reservation> findByCustomer(Customer customer) {
        return reservations.values().stream()
                .filter(r -> r.getCustomer().equals(customer))
                .collect(Collectors.toList());
    }

    public List<Reservation> findByCar(Car car) {
        return reservations.values().stream()
                .filter(r -> r.getCar().equals(car))
                .collect(Collectors.toList());
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservations.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Find active reservations for a car that overlap with given date range
     */
    public List<Reservation> findActiveReservationsForCar(Car car, LocalDate startDate, LocalDate endDate) {
        return reservations.values().stream()
                .filter(r -> r.getCar().equals(car))
                .filter(Reservation::isActive)
                .filter(r -> r.overlaps(startDate, endDate))
                .collect(Collectors.toList());
    }

    /**
     * Find all active reservations (not cancelled or completed)
     */
    public List<Reservation> findActiveReservations() {
        return reservations.values().stream()
                .filter(Reservation::isActive)
                .collect(Collectors.toList());
    }

    public void delete(String reservationId) {
        reservations.remove(reservationId);
    }

    public int count() {
        return reservations.size();
    }
}
