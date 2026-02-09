package carrentalsystem.repository;

import carrentalsystem.enums.CarStatus;
import carrentalsystem.enums.CarType;
import carrentalsystem.model.Car;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for Car data access
 * Thread-safe using ConcurrentHashMap
 */
public class CarRepository {
    private final Map<String, Car> cars;  // licensePlate -> Car

    public CarRepository() {
        this.cars = new ConcurrentHashMap<>();
    }

    public void save(Car car) {
        cars.put(car.getLicensePlate(), car);
    }

    public Optional<Car> findByLicensePlate(String licensePlate) {
        return Optional.ofNullable(cars.get(licensePlate));
    }

    public List<Car> findAll() {
        return new ArrayList<>(cars.values());
    }

    public List<Car> findByMakeAndModel(String make, String model) {
        return cars.values().stream()
                .filter(car -> car.getMake().equalsIgnoreCase(make) && 
                              car.getModel().equalsIgnoreCase(model))
                .collect(Collectors.toList());
    }

    public List<Car> findByType(CarType type) {
        return cars.values().stream()
                .filter(car -> car.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Car> findByPriceRange(double minPrice, double maxPrice) {
        return cars.values().stream()
                .filter(car -> car.getRentalPricePerDay() >= minPrice && 
                              car.getRentalPricePerDay() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<Car> findByStatus(CarStatus status) {
        return cars.values().stream()
                .filter(car -> car.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void delete(String licensePlate) {
        cars.remove(licensePlate);
    }

    public int count() {
        return cars.size();
    }
}
