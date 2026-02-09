package carrentalsystem.model;

import carrentalsystem.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a car reservation
 * Immutable once created (except status updates)
 */
public class Reservation {
    private final String reservationId;
    private final Customer customer;
    private final Car car;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double totalPrice;
    private final LocalDateTime createdAt;
    
    // Mutable state
    private ReservationStatus status;
    private LocalDateTime pickupTime;
    private LocalDateTime returnTime;
    private int startMileage;
    private int endMileage;

    private Reservation(Builder builder) {
        this.reservationId = builder.reservationId;
        this.customer = builder.customer;
        this.car = builder.car;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.totalPrice = builder.totalPrice;
        this.createdAt = builder.createdAt;
        this.status = builder.status;
    }

    // Getters
    public String getReservationId() {
        return reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Car getCar() {
        return car;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public LocalDateTime getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

    public int getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(int startMileage) {
        this.startMileage = startMileage;
    }

    public int getEndMileage() {
        return endMileage;
    }

    public void setEndMileage(int endMileage) {
        this.endMileage = endMileage;
    }

    /**
     * Get number of rental days
     */
    public long getRentalDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Check if this reservation overlaps with given date range
     */
    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        return !startDate.isAfter(otherEnd) && !endDate.isBefore(otherStart);
    }

    /**
     * Check if reservation is active (not cancelled or completed)
     */
    public boolean isActive() {
        return status != ReservationStatus.CANCELLED && 
               status != ReservationStatus.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + reservationId + '\'' +
                ", customer=" + customer.getName() +
                ", car=" + car +
                ", dates=" + startDate + " to " + endDate +
                ", status=" + status +
                ", price=" + totalPrice +
                '}';
    }

    // Builder Pattern
    public static class Builder {
        private String reservationId;
        private Customer customer;
        private Car car;
        private LocalDate startDate;
        private LocalDate endDate;
        private double totalPrice;
        private LocalDateTime createdAt = LocalDateTime.now();
        private ReservationStatus status = ReservationStatus.PENDING_PAYMENT;

        public Builder reservationId(String reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public Builder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder car(Car car) {
            this.car = car;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder totalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            // Validation
            if (reservationId == null || reservationId.trim().isEmpty()) {
                throw new IllegalArgumentException("Reservation ID is required");
            }
            if (customer == null) {
                throw new IllegalArgumentException("Customer is required");
            }
            if (car == null) {
                throw new IllegalArgumentException("Car is required");
            }
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Start and end dates are required");
            }
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date must be before or equal to end date");
            }
            if (startDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Start date cannot be in the past");
            }
            if (totalPrice < 0) {
                throw new IllegalArgumentException("Total price cannot be negative");
            }

            return new Reservation(this);
        }
    }
}
