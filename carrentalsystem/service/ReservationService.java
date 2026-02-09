package carrentalsystem.service;

import carrentalsystem.enums.CarStatus;
import carrentalsystem.enums.ReservationStatus;
import carrentalsystem.exception.*;
import carrentalsystem.model.Car;
import carrentalsystem.model.Customer;
import carrentalsystem.model.Reservation;
import carrentalsystem.repository.CarRepository;
import carrentalsystem.repository.ReservationRepository;
import carrentalsystem.strategy.PaymentStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service for managing reservations
 * 
 * Design Patterns:
 * - Strategy Pattern: For payment processing
 * - Repository Pattern: For data access
 * - Builder Pattern: For creating reservations
 * 
 * Thread Safety:
 * - Uses ReadWriteLock for concurrent access
 * - Atomic reservation creation with payment
 */
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ReservationService(ReservationRepository reservationRepository, CarRepository carRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
    }

    /**
     * Create a reservation with payment (atomic operation)
     * 
     * This fixes the bug in the original design where reservation was created
     * before payment, leading to inconsistent state.
     */
    public Reservation createReservation(Customer customer, Car car, 
                                        LocalDate startDate, LocalDate endDate,
                                        PaymentStrategy paymentStrategy) 
            throws CarRentalException {
        
        lock.writeLock().lock();
        try {
            // Step 1: Validate
            validateReservation(customer, car, startDate, endDate);
            
            // Step 2: Check availability
            if (!isCarAvailable(car, startDate, endDate)) {
                throw new CarNotAvailableException(car.getLicensePlate(), startDate, endDate);
            }
            
            // Step 3: Calculate price
            double totalPrice = calculatePrice(car, startDate, endDate);
            
            // Step 4: Process payment BEFORE creating reservation
            String transactionId;
            try {
                transactionId = paymentStrategy.processPayment(totalPrice, customer.getCustomerId());
            } catch (PaymentFailedException e) {
                throw new PaymentFailedException("Payment failed: " + e.getMessage());
            }
            
            // Step 5: Create reservation (only after successful payment)
            String reservationId = generateReservationId();
            Reservation reservation = new Reservation.Builder()
                    .reservationId(reservationId)
                    .customer(customer)
                    .car(car)
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalPrice(totalPrice)
                    .status(ReservationStatus.CONFIRMED)  // Confirmed because payment succeeded
                    .build();
            
            // Step 6: Save reservation
            reservationRepository.save(reservation);
            
            // Step 7: Update car status (but NOT the availability flag!)
            // The car is still "available" in the fleet, just booked for these dates
            car.setStatus(CarStatus.RENTED);
            carRepository.save(car);
            
            System.out.println("✓ Reservation created: " + reservationId);
            System.out.println("  Transaction ID: " + transactionId);
            
            return reservation;
            
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Check if car is available for given date range
     * 
     * This is the CORRECT way to check availability:
     * - Check against ALL reservations for this car
     * - Check for date overlaps
     * - Don't use a boolean flag!
     */
    public boolean isCarAvailable(Car car, LocalDate startDate, LocalDate endDate) {
        lock.readLock().lock();
        try {
            // Check if car is in active fleet
            if (!car.isInActiveFleet()) {
                return false;
            }
            
            // Check for overlapping reservations
            List<Reservation> overlapping = reservationRepository
                    .findActiveReservationsForCar(car, startDate, endDate);
            
            return overlapping.isEmpty();
            
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Cancel a reservation
     */
    public void cancelReservation(String reservationId, PaymentStrategy paymentStrategy) 
            throws CarRentalException {
        
        lock.writeLock().lock();
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ReservationNotFoundException(reservationId));
            
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new InvalidReservationException("Reservation already cancelled");
            }
            
            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                throw new InvalidReservationException("Cannot cancel completed reservation");
            }
            
            // Process refund if payment was made
            if (reservation.getStatus() == ReservationStatus.CONFIRMED || 
                reservation.getStatus() == ReservationStatus.IN_PROGRESS) {
                try {
                    paymentStrategy.refund("TXN-" + reservationId, reservation.getTotalPrice());
                } catch (PaymentFailedException e) {
                    System.err.println("Warning: Refund failed: " + e.getMessage());
                }
            }
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            
            // Update car status back to available
            Car car = reservation.getCar();
            car.setStatus(CarStatus.AVAILABLE);
            carRepository.save(car);
            
            System.out.println("✓ Reservation cancelled: " + reservationId);
            
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Pick up car (start rental)
     */
    public void pickupCar(String reservationId, int startMileage) throws CarRentalException {
        lock.writeLock().lock();
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ReservationNotFoundException(reservationId));
            
            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                throw new InvalidReservationException("Reservation must be confirmed to pickup");
            }
            
            LocalDate today = LocalDate.now();
            if (today.isBefore(reservation.getStartDate())) {
                throw new InvalidReservationException("Cannot pickup before start date");
            }
            
            reservation.setStatus(ReservationStatus.IN_PROGRESS);
            reservation.setPickupTime(LocalDateTime.now());
            reservation.setStartMileage(startMileage);
            reservationRepository.save(reservation);
            
            System.out.println("✓ Car picked up: " + reservation.getCar());
            
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Return car (end rental)
     */
    public void returnCar(String reservationId, int endMileage) throws CarRentalException {
        lock.writeLock().lock();
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ReservationNotFoundException(reservationId));
            
            if (reservation.getStatus() != ReservationStatus.IN_PROGRESS) {
                throw new InvalidReservationException("Reservation must be in progress to return");
            }
            
            reservation.setStatus(ReservationStatus.COMPLETED);
            reservation.setReturnTime(LocalDateTime.now());
            reservation.setEndMileage(endMileage);
            reservationRepository.save(reservation);
            
            // Update car
            Car car = reservation.getCar();
            car.setStatus(CarStatus.AVAILABLE);
            car.setMileage(endMileage);
            carRepository.save(car);
            
            System.out.println("✓ Car returned: " + car);
            System.out.println("  Miles driven: " + (endMileage - reservation.getStartMileage()));
            
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Modify reservation dates
     */
    public Reservation modifyReservation(String reservationId, LocalDate newStartDate, 
                                        LocalDate newEndDate, PaymentStrategy paymentStrategy) 
            throws CarRentalException {
        
        lock.writeLock().lock();
        try {
            Reservation oldReservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ReservationNotFoundException(reservationId));
            
            if (oldReservation.getStatus() != ReservationStatus.CONFIRMED) {
                throw new InvalidReservationException("Can only modify confirmed reservations");
            }
            
            // Check if car is available for new dates (excluding this reservation)
            reservationRepository.delete(reservationId);
            
            try {
                if (!isCarAvailable(oldReservation.getCar(), newStartDate, newEndDate)) {
                    // Restore old reservation
                    reservationRepository.save(oldReservation);
                    throw new CarNotAvailableException(
                        oldReservation.getCar().getLicensePlate(), newStartDate, newEndDate);
                }
                
                // Calculate price difference
                double oldPrice = oldReservation.getTotalPrice();
                double newPrice = calculatePrice(oldReservation.getCar(), newStartDate, newEndDate);
                double priceDiff = newPrice - oldPrice;
                
                // Process payment difference
                if (priceDiff > 0) {
                    paymentStrategy.processPayment(priceDiff, oldReservation.getCustomer().getCustomerId());
                } else if (priceDiff < 0) {
                    paymentStrategy.refund("TXN-" + reservationId, Math.abs(priceDiff));
                }
                
                // Create new reservation
                Reservation newReservation = new Reservation.Builder()
                        .reservationId(reservationId)  // Keep same ID
                        .customer(oldReservation.getCustomer())
                        .car(oldReservation.getCar())
                        .startDate(newStartDate)
                        .endDate(newEndDate)
                        .totalPrice(newPrice)
                        .status(ReservationStatus.CONFIRMED)
                        .build();
                
                reservationRepository.save(newReservation);
                
                System.out.println("✓ Reservation modified: " + reservationId);
                return newReservation;
                
            } catch (Exception e) {
                // Restore old reservation on any error
                reservationRepository.save(oldReservation);
                throw e;
            }
            
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Helper methods
    
    private void validateReservation(Customer customer, Car car, LocalDate startDate, LocalDate endDate) 
            throws InvalidReservationException {
        
        if (!customer.hasValidLicense()) {
            throw new InvalidReservationException("Customer does not have a valid driver's license");
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidReservationException("Start date cannot be in the past");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new InvalidReservationException("Start date must be before or equal to end date");
        }
        
        if (!car.isInActiveFleet()) {
            throw new InvalidReservationException("Car is not available for rental");
        }
    }

    private double calculatePrice(Car car, LocalDate startDate, LocalDate endDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return car.getRentalPricePerDay() * days;
    }

    private String generateReservationId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Query methods
    
    public Reservation getReservation(String reservationId) throws ReservationNotFoundException {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    public List<Reservation> getCustomerReservations(Customer customer) {
        return reservationRepository.findByCustomer(customer);
    }

    public List<Reservation> getActiveReservations() {
        return reservationRepository.findActiveReservations();
    }
}
