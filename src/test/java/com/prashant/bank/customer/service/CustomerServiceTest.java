package com.prashant.bank.customer.service;

import com.prashant.bank.customer.dto.CustomerRequestDto;
import com.prashant.bank.customer.dto.CustomerResponseDto;
import com.prashant.bank.customer.entity.Customer;
import com.prashant.bank.customer.exception.CustomerAlreadyExistsException;
import com.prashant.bank.customer.exception.CustomerNotFoundException;
import com.prashant.bank.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDto request;

    @BeforeEach
    void setUp() {

        request = new CustomerRequestDto(
                "Prashant Mishra",
                "prashant@example.com",
                "9876543210",
                "Greater Noida"
        );
    }

    @Test
    void shouldCreateCustomerSuccessfully() {

        Customer customer = new Customer();

        customer.setCustomerId(1L);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());
        customer.setAddress(request.getAddress());
        customer.setActive(true);

        when(customerRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(customerRepository.findByMobile(request.getMobile()))
                .thenReturn(Optional.empty());

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

        CustomerResponseDto response =
                customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals("Prashant Mishra", response.getName());
        assertEquals("prashant@example.com", response.getEmail());
        assertEquals("9876543210", response.getMobile());
        assertTrue(response.getActive());

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        Customer existingCustomer = new Customer();

        when(customerRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existingCustomer));

        assertThrows(
                CustomerAlreadyExistsException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(99L)
        );
    }

    @Test
    void shouldDeactivateCustomer() {

        Customer customer = new Customer();

        customer.setCustomerId(1L);
        customer.setActive(true);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        assertFalse(customer.getActive());

        verify(customerRepository).save(customer);
    }
}