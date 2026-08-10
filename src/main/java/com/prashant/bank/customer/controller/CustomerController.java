package com.prashant.bank.customer.controller;

import com.prashant.bank.customer.dto.CustomerRequestDto;
import com.prashant.bank.customer.dto.CustomerResponseDto;
import com.prashant.bank.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Create a new customer.
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid @RequestBody CustomerRequestDto request) {

        CustomerResponseDto response =
                customerService.createCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get all active customers.
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {

        List<CustomerResponseDto> customers =
                customerService.getAllCustomers();

        return ResponseEntity.ok(customers);
    }

    /**
     * Get customer by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @PathVariable Long id) {

        CustomerResponseDto response =
                customerService.getCustomerById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Update customer.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto request) {

        CustomerResponseDto response =
                customerService.updateCustomer(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate customer.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }
}