package carrentalsystem.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a customer in the car rental system
 */
public class Customer {
    private final String customerId;
    private final String name;
    private final String email;
    private final String phone;
    private final String driversLicenseNumber;
    private final LocalDate licenseExpiryDate;

    private Customer(Builder builder) {
        this.customerId = builder.customerId;
        this.name = builder.name;
        this.email = builder.email;
        this.phone = builder.phone;
        this.driversLicenseNumber = builder.driversLicenseNumber;
        this.licenseExpiryDate = builder.licenseExpiryDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    /**
     * Check if driver's license is valid
     */
    public boolean hasValidLicense() {
        return licenseExpiryDate != null && licenseExpiryDate.isAfter(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    // Builder Pattern
    public static class Builder {
        private String customerId;
        private String name;
        private String email;
        private String phone;
        private String driversLicenseNumber;
        private LocalDate licenseExpiryDate;

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder driversLicenseNumber(String driversLicenseNumber) {
            this.driversLicenseNumber = driversLicenseNumber;
            return this;
        }

        public Builder licenseExpiryDate(LocalDate licenseExpiryDate) {
            this.licenseExpiryDate = licenseExpiryDate;
            return this;
        }

        public Customer build() {
            // Validation
            if (customerId == null || customerId.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Valid email is required");
            }
            if (driversLicenseNumber == null || driversLicenseNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Driver's license number is required");
            }

            return new Customer(this);
        }
    }
}
