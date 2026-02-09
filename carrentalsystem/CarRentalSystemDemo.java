package carrentalsystem;

import carrentalsystem.enums.CarType;
import carrentalsystem.exception.CarRentalException;
import carrentalsystem.model.Car;
import carrentalsystem.model.Customer;
import carrentalsystem.model.Reservation;
import carrentalsystem.strategy.CreditCardPayment;
import carrentalsystem.strategy.PaymentStrategy;

import java.time.LocalDate;
import java.util.List;

/**
 * Demo showcasing the improved Car Rental System
 */
public class CarRentalSystemDemo {

    public static void main(String[] args) {
        System.out.println("=== Car Rental System - Clean Design Demo ===\n");

        CarRentalSystem system = new CarRentalSystem();
        PaymentStrategy payment = new CreditCardPayment();

        try {
            // Setup
            setupFleet(system);
            setupCustomers(system);

            // Scenario 1: Successful reservation
            System.out.println("\n--- SCENARIO 1: Successful Reservation ---");
            scenario1_SuccessfulReservation(system, payment);

            // Scenario 2: Overlapping reservation (should fail)
            System.out.println("\n--- SCENARIO 2: Overlapping Reservation (Should Fail) ---");
            scenario2_OverlappingReservation(system, payment);

            // Scenario 3: Non-overlapping reservation (should succeed)
            System.out.println("\n--- SCENARIO 3: Non-Overlapping Reservation (Should Succeed) ---");
            scenario3_NonOverlappingReservation(system, payment);

            // Scenario 4: Modify reservation
            System.out.println("\n--- SCENARIO 4: Modify Reservation ---");
            scenario4_ModifyReservation(system, payment);

            // Scenario 5: Complete rental lifecycle
            System.out.println("\n--- SCENARIO 5: Complete Rental Lifecycle ---");
            scenario5_CompleteLifecycle(system, payment);

            // Summary
            System.out.println("\n--- SUMMARY ---");
            printSummary(system);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Demo Complete ===");
    }

    private static void setupFleet(CarRentalSystem system) {
        System.out.println("🚗 Setting up fleet...");

        Car car1 = new Car.Builder()
                .licensePlate("ABC123")
                .make("Toyota")
                .model("Camry")
                .year(2022)
                .type(CarType.SEDAN)
                .rentalPricePerDay(50.0)
                .mileage(15000)
                .build();

        Car car2 = new Car.Builder()
                .licensePlate("XYZ789")
                .make("Honda")
                .model("CR-V")
                .year(2023)
                .type(CarType.SUV)
                .rentalPricePerDay(75.0)
                .mileage(8000)
                .build();

        Car car3 = new Car.Builder()
                .licensePlate("DEF456")
                .make("Ford")
                .model("Mustang")
                .year(2024)
                .type(CarType.SPORTS)
                .rentalPricePerDay(120.0)
                .mileage(2000)
                .build();

        system.addCar(car1);
        system.addCar(car2);
        system.addCar(car3);

        System.out.println("✓ Fleet ready: " + system.getAllCars().size() + " cars\n");
    }

    private static void setupCustomers(CarRentalSystem system) {
        System.out.println("👤 Registering customers...");

        Customer customer1 = new Customer.Builder()
                .customerId("CUST001")
                .name("John Doe")
                .email("john@example.com")
                .phone("555-0001")
                .driversLicenseNumber("DL123456")
                .licenseExpiryDate(LocalDate.now().plusYears(2))
                .build();

        Customer customer2 = new Customer.Builder()
                .customerId("CUST002")
                .name("Jane Smith")
                .email("jane@example.com")
                .phone("555-0002")
                .driversLicenseNumber("DL789012")
                .licenseExpiryDate(LocalDate.now().plusYears(3))
                .build();

        system.registerCustomer(customer1);
        system.registerCustomer(customer2);

        System.out.println("✓ Customers registered\n");
    }

    private static void scenario1_SuccessfulReservation(CarRentalSystem system, PaymentStrategy payment) 
            throws CarRentalException {
        
        Customer customer = system.getCustomer("CUST001");
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        System.out.println("📅 Searching for Toyota Camry from " + startDate + " to " + endDate);
        List<Car> availableCars = system.searchCars("Toyota", "Camry", startDate, endDate);

        if (!availableCars.isEmpty()) {
            Car car = availableCars.get(0);
            System.out.println("✓ Found: " + car);

            Reservation reservation = system.makeReservation(customer, car, startDate, endDate, payment);
            System.out.println("✓ Reservation successful!");
            System.out.println("  " + reservation);
        }
    }

    private static void scenario2_OverlappingReservation(CarRentalSystem system, PaymentStrategy payment) {
        
        Customer customer = system.getCustomer("CUST002");
        LocalDate startDate = LocalDate.now().plusDays(2);  // Overlaps with previous reservation
        LocalDate endDate = startDate.plusDays(2);

        System.out.println("📅 Trying to book Toyota Camry from " + startDate + " to " + endDate);
        
        try {
            List<Car> availableCars = system.searchCars("Toyota", "Camry", startDate, endDate);
            
            if (availableCars.isEmpty()) {
                System.out.println("✓ Correctly rejected: Car not available (already booked)");
            } else {
                Car car = availableCars.get(0);
                system.makeReservation(customer, car, startDate, endDate, payment);
                System.out.println("❌ ERROR: Should have been rejected!");
            }
        } catch (CarRentalException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }
    }

    private static void scenario3_NonOverlappingReservation(CarRentalSystem system, PaymentStrategy payment) 
            throws CarRentalException {
        
        Customer customer = system.getCustomer("CUST002");
        LocalDate startDate = LocalDate.now().plusDays(10);  // After first reservation
        LocalDate endDate = startDate.plusDays(2);

        System.out.println("📅 Booking Toyota Camry from " + startDate + " to " + endDate);
        List<Car> availableCars = system.searchCars("Toyota", "Camry", startDate, endDate);

        if (!availableCars.isEmpty()) {
            Car car = availableCars.get(0);
            Reservation reservation = system.makeReservation(customer, car, startDate, endDate, payment);
            System.out.println("✓ Reservation successful! (Same car, different dates)");
            System.out.println("  " + reservation);
        }
    }

    private static void scenario4_ModifyReservation(CarRentalSystem system, PaymentStrategy payment) 
            throws CarRentalException {
        
        Customer customer = system.getCustomer("CUST001");
        List<Reservation> reservations = system.getCustomerReservations(customer);

        if (!reservations.isEmpty()) {
            Reservation original = reservations.get(0);
            System.out.println("📝 Original: " + original);

            LocalDate newStartDate = original.getStartDate().plusDays(1);
            LocalDate newEndDate = original.getEndDate().plusDays(1);

            System.out.println("📝 Modifying to: " + newStartDate + " to " + newEndDate);
            
            Reservation modified = system.modifyReservation(
                original.getReservationId(), newStartDate, newEndDate, payment);
            
            System.out.println("✓ Reservation modified!");
            System.out.println("  " + modified);
        }
    }

    private static void scenario5_CompleteLifecycle(CarRentalSystem system, PaymentStrategy payment) 
            throws CarRentalException {
        
        System.out.println("🔄 Demonstrating complete rental lifecycle:");

        Customer customer = system.getCustomer("CUST001");
        LocalDate startDate = LocalDate.now().plusDays(20);
        LocalDate endDate = startDate.plusDays(5);

        // 1. Make reservation
        List<Car> cars = system.searchCarsByType(CarType.SUV, startDate, endDate);
        if (!cars.isEmpty()) {
            Car car = cars.get(0);
            Reservation reservation = system.makeReservation(customer, car, startDate, endDate, payment);
            System.out.println("  1. ✓ Reservation created: " + reservation.getReservationId());

            // 2. Pickup car
            system.pickupCar(reservation.getReservationId(), car.getMileage());
            System.out.println("  2. ✓ Car picked up");

            // 3. Return car
            int endMileage = car.getMileage() + 250;
            system.returnCar(reservation.getReservationId(), endMileage);
            System.out.println("  3. ✓ Car returned");

            Reservation completed = system.getReservation(reservation.getReservationId());
            System.out.println("  Final status: " + completed.getStatus());
        }
    }

    private static void printSummary(CarRentalSystem system) {
        System.out.println("\n📊 System Summary:");
        System.out.println("  Total cars: " + system.getAllCars().size());
        System.out.println("  Active reservations: " + system.getActiveReservations().size());

        System.out.println("\n📋 All Reservations:");
        for (Reservation r : system.getActiveReservations()) {
            System.out.println("  " + r);
        }

        System.out.println("\n🎯 Key Improvements Demonstrated:");
        System.out.println("  ✓ Date-based availability (not boolean flag)");
        System.out.println("  ✓ Atomic reservation with payment");
        System.out.println("  ✓ Proper exception handling");
        System.out.println("  ✓ Reservation lifecycle management");
        System.out.println("  ✓ Reservation modification");
        System.out.println("  ✓ Thread-safe operations");
        System.out.println("  ✓ No Singleton anti-pattern");
    }
}
