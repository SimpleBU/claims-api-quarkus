package com.example.claims.resource;

import com.example.claims.dto.ContactPatchRequest;
import com.example.claims.dto.Customer;
import com.example.claims.dto.CustomerCreateRequest;
import com.example.claims.dto.CustomerUpdateRequest;
import com.example.claims.dto.PageResult;
import com.example.claims.service.CustomerService;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

public class CustomerResource implements CustomerApi {

    private final CustomerService customerService;

    public CustomerResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public PageResult<Customer> list(String lastName, int offset, int limit) {
        List<Customer> found = customerService.findAll(lastName);
        return PageResult.of(found, offset, limit);
    }

    @Override
    public Customer getOne(String id) {
        return customerService.findById(id);
    }

    @Override
    public Response create(CustomerCreateRequest request) {
        Customer customer = customerService.create(request);
        return Response.created(URI.create("/api/v1/customers/" + customer.id()))
                .entity(customer)
                .build();
    }

    @Override
    public Customer replace(String id, CustomerUpdateRequest request) {
        return customerService.replace(id, request);
    }

    @Override
    public Customer patchContacts(String id, ContactPatchRequest request) {
        return customerService.patchContact(id, request);
    }
}
