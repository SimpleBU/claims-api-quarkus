package com.example.claims.service;

import com.example.claims.dto.Contact;
import com.example.claims.dto.ContactPatchRequest;
import com.example.claims.dto.Customer;
import com.example.claims.dto.CustomerCreateRequest;
import com.example.claims.dto.CustomerUpdateRequest;
import com.example.claims.exception.ResourceNotFoundException;
import com.example.claims.model.IdSequence;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CustomerService {

    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final IdSequence ids = new IdSequence("cus", 0);

    public CustomerService() {
        seed("Ivan", "Ivanov", "ivan.ivanov@example.com", "+79161234567", "770123456789");
        seed("Olga", "Kuznetsova", "olga.k@example.com", "+79031112233", "770987654321");
        seed("Pavel", "Sidorov", "pavel.sidorov@example.com", "+79219998877", "781122334455");
    }

    private void seed(String firstName, String lastName, String email, String phone, String taxNumber) {
        String id = ids.next();
        customers.put(id, new Customer(id, firstName, lastName, LocalDate.of(1985, 4, 12),
                new Contact(email, phone, "EMAIL"), taxNumber, OffsetDateTime.now().minusYears(2)));
    }

    public List<Customer> findAll(String lastName) {
        return customers.values().stream()
                .filter(c -> lastName == null || c.lastName().equalsIgnoreCase(lastName))
                .sorted(Comparator.comparing(Customer::lastName))
                .toList();
    }

    public Customer findById(String id) {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer", id);
        }
        return customer;
    }

    public Customer create(CustomerCreateRequest request) {
        String id = ids.next();
        Customer customer = new Customer(id, request.firstName(), request.lastName(), request.birthDate(),
                request.contact(), request.taxNumber(), OffsetDateTime.now());
        customers.put(id, customer);
        return customer;
    }

    public Customer replace(String id, CustomerUpdateRequest request) {
        Customer current = findById(id);
        Customer updated = new Customer(id, request.firstName(), request.lastName(), current.birthDate(),
                request.contact(), current.taxNumber(), current.registeredAt());
        customers.put(id, updated);
        return updated;
    }

    public Customer patchContact(String id, ContactPatchRequest request) {
        Customer current = findById(id);
        Contact contact = current.contact();
        Contact merged = new Contact(
                request.email() == null ? contact.email() : request.email(),
                request.phone() == null ? contact.phone() : request.phone(),
                request.preferredChannel() == null ? contact.preferredChannel() : request.preferredChannel());
        Customer updated = current.withContact(merged);
        customers.put(id, updated);
        return updated;
    }

    public int size() {
        return customers.size();
    }
}
