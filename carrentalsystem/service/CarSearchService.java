package carrentalsystem.service;

import carrentalsystem.enums.CarType;
import carrentalsystem.model.Car;
import carrentalsystem.repository.CarRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching cars
 * Provides various search criteria
 */
public class CarSearchService {
    private final CarRepository carRepository;
    private final ReservationService reservationService;

    public CarSearchService(CarRepository carRepository, ReservationService reservationService) {
        this.carRepository = carRepository;
        this.reservationService = reservationService;
    }

    /**
     * Search available cars by make and model for given dates
     */
    public List<Car> searchByMakeAndModel(String make, String model, 
                                          LocalDate startDate, LocalDate endDate) {
        List<Car> cars = carRepository.findByMakeAndModel(make, model);
        return filterAvailableCars(cars, startDate, endDate);
    }

    /**
     * Search available cars by type for given dates
     */
    public List<Car> searchByType(CarType type, LocalDate startDate, LocalDate endDate) {
        List<Car> cars = carRepository.findByType(type);
        return filterAvailableCars(cars, startDate, endDate);
    }

    /**
     * Search available cars by price range for given dates
     */
    public List<Car> searchByPriceRange(double minPrice, double maxPrice, 
                                       LocalDate startDate, LocalDate endDate) {
        List<Car> cars = carRepository.findByPriceRange(minPrice, maxPrice);
        return filterAvailableCars(cars, startDate, endDate);
    }

    /**
     * Search all available cars for given dates
     */
    public List<Car> searchAvailableCars(LocalDate startDate, LocalDate endDate) {
        List<Car> allCars = carRepository.findAll();
        return filterAvailableCars(allCars, startDate, endDate);
    }

    /**
     * Filter cars that are available for given date range
     */
    private List<Car> filterAvailableCars(List<Car> cars, LocalDate startDate, LocalDate endDate) {
        return cars.stream()
                .filter(car -> reservationService.isCarAvailable(car, startDate, endDate))
                .collect(Collectors.toList());
    }

    /**
     * Get all cars in fleet
     */
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
}
