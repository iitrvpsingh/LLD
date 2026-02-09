package carrentalsystem.model;

import carrentalsystem.enums.CarStatus;
import carrentalsystem.enums.CarType;

import java.util.Objects;

/**
 * Represents a car in the rental fleet
 * 
 * Design Notes:
 * - Removed boolean 'available' flag (was causing bugs)
 * - Availability is now determined by checking reservations
 * - CarStatus is for fleet management (maintenance, retired)
 * - Immutable fields for car identity
 */
public class Car {
    private final String licensePlate;  // Unique identifier
    private final String make;
    private final String model;
    private final int year;
    private final CarType type;
    private final double rentalPricePerDay;
    
    // Mutable state
    private CarStatus status;
    private int mileage;

    private Car(Builder builder) {
        this.licensePlate = builder.licensePlate;
        this.make = builder.make;
        this.model = builder.model;
        this.year = builder.year;
        this.type = builder.type;
        this.rentalPricePerDay = builder.rentalPricePerDay;
        this.status = builder.status;
        this.mileage = builder.mileage;
    }

    // Getters
    public String getLicensePlate() {
        return licensePlate;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public CarType getType() {
        return type;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    /**
     * Check if car is in active fleet (not in maintenance or retired)
     */
    public boolean isInActiveFleet() {
        return status == CarStatus.AVAILABLE || status == CarStatus.RENTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(licensePlate, car.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licensePlate);
    }

    @Override
    public String toString() {
        return year + " " + make + " " + model + " (" + licensePlate + ")";
    }

    // Builder Pattern
    public static class Builder {
        private String licensePlate;
        private String make;
        private String model;
        private int year;
        private CarType type;
        private double rentalPricePerDay;
        private CarStatus status = CarStatus.AVAILABLE;
        private int mileage = 0;

        public Builder licensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
            return this;
        }

        public Builder make(String make) {
            this.make = make;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder type(CarType type) {
            this.type = type;
            return this;
        }

        public Builder rentalPricePerDay(double rentalPricePerDay) {
            this.rentalPricePerDay = rentalPricePerDay;
            return this;
        }

        public Builder status(CarStatus status) {
            this.status = status;
            return this;
        }

        public Builder mileage(int mileage) {
            this.mileage = mileage;
            return this;
        }

        public Car build() {
            // Validation
            if (licensePlate == null || licensePlate.trim().isEmpty()) {
                throw new IllegalArgumentException("License plate is required");
            }
            if (make == null || make.trim().isEmpty()) {
                throw new IllegalArgumentException("Make is required");
            }
            if (model == null || model.trim().isEmpty()) {
                throw new IllegalArgumentException("Model is required");
            }
            if (year < 1900 || year > 2100) {
                throw new IllegalArgumentException("Invalid year: " + year);
            }
            if (type == null) {
                throw new IllegalArgumentException("Car type is required");
            }
            if (rentalPricePerDay <= 0) {
                throw new IllegalArgumentException("Rental price must be positive");
            }

            return new Car(this);
        }
    }
}
