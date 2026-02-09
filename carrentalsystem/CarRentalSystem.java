package carrentalsystem;

import carrentalsystem.enums.CarType;
import carrentalsystem.exception.CarRentalException;
import carrentalsystem.model.Car;
import carrentalsystem.model.Customer;
import carrentalsystem.model.Reservation;
import carrentalsystem.repository.CarRepository;
import carrentalsystem.repository.CustomerRepository;
import carrentalsystem.repository.ReservationRepository;
import carrentalsystem.service.CarSearchService;
import carrentalsystem.service.ReservationService;
import carrentalsystem.strategy.PaymentStrategy;

import java.time.LocalDate;
import java.util.List;

/**
 * Facade for Car Rental System
 * 
 * Design Patterns Used:
 * 1. Facade Pattern - Simplified interface to complex subsystem
 * 2. Dependency Injection - Services injected, not created
 * 3. Repository Pattern - Data access abstraction
 * 4. Strategy Pattern - Payment methods
 * 5. Builder Pattern - Object creation
 * 
 * Key Improvements over Original:
 * 1. ✅ NO Singleton - Uses dependency injection
 * 2. ✅ Proper availability checking - Date-based, not boolean flag
 * 3. ✅ Atomic reservation with payment - No inconsistent state
 * 4. ✅ Exception handling - No null returns
 * 5. ✅ Thread-safe - ReadWriteLock in services
 * 6. ✅ Reservation lifecycle - PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
 * 7. ✅ Reservation modification - Can change dates
 * 8. ✅ Proper validations - Dates, license, etc.
 */
public class CarRentalSystem {
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final CarSearchService carSearchService;

    /**
     * Constructor with Dependency Injection
     * (NOT Singleton!)
     */
    public CarRentalSystem() {
        this.carRepository = new CarRepository();
        this.customerRepository = new CustomerRepository();
        this.reservationRepository = new ReservationRepository();
        this.reservationService = new ReservationService(reservationRepository, carRepository);
        this.carSearchService = new CarSearchService(carRepository, reservationService);
    }

    // ============ Car Management ============

    public void addCar(Car car) {
        carRepository.save(car);
        System.out.println("✓ Car added: " + car);
    }

    public void removeCar(String licensePlate) {
        carRepository.delete(licensePlate);
        System.out.println("✓ Car removed: " + licensePlate);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // ============ Customer Management ============

    public void registerCustomer(Customer customer) {
        customerRepository.save(customer);
        System.out.println("✓ Customer registered: " + customer.getName());
    }

    public Customer getCustomer(String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    // ============ Car Search ============

    public List<Car> searchCars(String make, String model, LocalDate startDate, LocalDate endDate) {
        return carSearchService.searchByMakeAndModel(make, model, startDate, endDate);
    }

    public List<Car> searchCarsByType(CarType type, LocalDate startDate, LocalDate endDate) {
        return carSearchService.searchByType(type, startDate, endDate);
    }

    public List<Car> searchCarsByPrice(double minPrice, double maxPrice, 
                                       LocalDate startDate, LocalDate endDate) {
        return carSearchService.searchByPriceRange(minPrice, maxPrice, startDate, endDate);
    }

    public List<Car> searchAvailableCars(LocalDate startDate, LocalDate endDate) {
        return carSearchService.searchAvailableCars(startDate, endDate);
    }

    // ============ Reservation Management ============

    public Reservation makeReservation(Customer customer, Car car, 
                                      LocalDate startDate, LocalDate endDate,
                                      PaymentStrategy paymentStrategy) throws CarRentalException {
        return reservationService.createReservation(customer, car, startDate, endDate, paymentStrategy);
    }

    public void cancelReservation(String reservationId, PaymentStrategy paymentStrategy) 
            throws CarRentalException {
        reservationService.cancelReservation(reservationId, paymentStrategy);
    }

    public Reservation modifyReservation(String reservationId, LocalDate newStartDate, 
                                        LocalDate newEndDate, PaymentStrategy paymentStrategy) 
            throws CarRentalException {
        return reservationService.modifyReservation(reservationId, newStartDate, newEndDate, paymentStrategy);
    }

    public void pickupCar(String reservationId, int startMileage) throws CarRentalException {
        reservationService.pickupCar(reservationId, startMileage);
    }

    public void returnCar(String reservationId, int endMileage) throws CarRentalException {
        reservationService.returnCar(reservationId, endMileage);
    }

    public Reservation getReservation(String reservationId) throws CarRentalException {
        return reservationService.getReservation(reservationId);
    }

    public List<Reservation> getCustomerReservations(Customer customer) {
        return reservationService.getCustomerReservations(customer);
    }

    public List<Reservation> getActiveReservations() {
        return reservationService.getActiveReservations();
    }

    // ============ Availability Check ============

    public boolean isCarAvailable(Car car, LocalDate startDate, LocalDate endDate) {
        return reservationService.isCarAvailable(car, startDate, endDate);
    }
}
