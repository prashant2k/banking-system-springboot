package com.prashant.bank.customer.service;

import com.prashant.bank.customer.dto.CustomerRequestDto;
import com.prashant.bank.customer.dto.CustomerResponseDto;
import com.prashant.bank.customer.entity.Customer;
import com.prashant.bank.customer.exception.CustomerAlreadyExistsException;
import com.prashant.bank.customer.exception.CustomerNotFoundException;
import com.prashant.bank.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Create a new customer.
     */
    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto request) {

        // Check duplicate email
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with email: " + request.getEmail()
            );
        }

        // Check duplicate mobile
        if (customerRepository.findByMobile(request.getMobile()).isPresent()) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with mobile: " + request.getMobile()
            );
        }

        Customer customer = new Customer();

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());
        customer.setAddress(request.getAddress());
        customer.setActive(true);

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    /**
     * Get all active customers.
     */
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {

        return customerRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get customer by ID.
     */
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + id
                        )
                );

        if (!customer.getActive()) {
            throw new CustomerNotFoundException(
                    "Customer is inactive with id: " + id
            );
        }

        return mapToResponse(customer);
    }

    /**
     * Update customer.
     */
    @Transactional
    public CustomerResponseDto updateCustomer(
            Long id,
            CustomerRequestDto requestDto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + id
                        )
                );

        if (!customer.getActive()) {
            throw new CustomerNotFoundException(
                    "Cannot update inactive customer with id: " + id
            );
        }

        // Check email uniqueness only when email is changed
        if (!customer.getEmail().equals(requestDto.getEmail())) {

            customerRepository.findByEmail(requestDto.getEmail())
                    .ifPresent(existingCustomer -> {
                        throw new CustomerAlreadyExistsException(
                                "Customer already exists with email: "
                                        + requestDto.getEmail()
                        );
                    });
        }

        // Check mobile uniqueness only when mobile is changed
        if (!customer.getMobile().equals(requestDto.getMobile())) {

            customerRepository.findByMobile(requestDto.getMobile())
                    .ifPresent(existingCustomer -> {
                        throw new CustomerAlreadyExistsException(
                                "Customer already exists with mobile: "
                                        + requestDto.getMobile()
                        );
                    });
        }

        customer.setName(requestDto.getName());
        customer.setEmail(requestDto.getEmail());
        customer.setMobile(requestDto.getMobile());
        customer.setAddress(requestDto.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    /**
     * Soft delete / deactivate customer.
     */
    @Transactional
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + id
                        )
                );

        if (!customer.getActive()) {
            throw new CustomerNotFoundException(
                    "Customer is already inactive with id: " + id
            );
        }

        customer.setActive(false);

        customerRepository.save(customer);
    }

    /**
     * Convert Customer entity to response DTO.
     */
    private CustomerResponseDto mapToResponse(Customer customer) {

        CustomerResponseDto response = new CustomerResponseDto();

        response.setCustomerId(customer.getCustomerId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setMobile(customer.getMobile());
        response.setAddress(customer.getAddress());
        response.setCreatedDate(customer.getCreatedDate());
        response.setUpdatedDate(customer.getUpdatedDate());
        response.setActive(customer.getActive());

        return response;
    }
}