package carrentalsystem.repository;

import carrentalsystem.model.Customer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for Customer data access
 * Thread-safe using ConcurrentHashMap
 */
public class CustomerRepository {
    private final Map<String, Customer> customers;  // customerId -> Customer

    public CustomerRepository() {
        this.customers = new ConcurrentHashMap<>();
    }

    public void save(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    public Optional<Customer> findByEmail(String email) {
        return customers.values().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    public void delete(String customerId) {
        customers.remove(customerId);
    }

    public int count() {
        return customers.size();
    }
}
