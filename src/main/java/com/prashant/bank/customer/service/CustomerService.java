package com.prashant.bank.customer.service;

import com.prashant.bank.customer.dto.CustomerRequestDto;
import com.prashant.bank.customer.dto.CustomerResponseDto;
import com.prashant.bank.customer.entity.Customer;
import com.prashant.bank.customer.exception.CustomerNotFoundException;
import com.prashant.bank.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponseDto createCustomer(CustomerRequestDto request){

        // Check Duplicate Email
        if(customerRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email Already Exists");
        }

        // Check Duplicate Mobile Number
        if(customerRepository.findByMobile(request.getMobile()).isPresent()){
            throw new RuntimeException("Mobile already Exists");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());
        customer.setAddress(request.getAddress());

        Customer savedCustomer = customerRepository.save(customer);

        CustomerResponseDto response = new CustomerResponseDto();
        response.setName(savedCustomer.getName());
        response.setEmail(savedCustomer.getEmail());
        response.setMobile(savedCustomer.getMobile());
        response.setAddress(savedCustomer.getAddress());
        response.setCustomerId(savedCustomer.getCustomerId());
        response.setCreatedDate(savedCustomer.getCreatedDate());

        return response;

    }

    public List<CustomerResponseDto> getAllCustomers() {

        List<Customer> customers = customerRepository.findAll();

        return customers.stream().map(customer -> {
            CustomerResponseDto response = new CustomerResponseDto();

            response.setCustomerId(customer.getCustomerId());
            response.setName(customer.getName());
            response.setEmail(customer.getEmail());
            response.setMobile(customer.getMobile());
            response.setAddress(customer.getAddress());
            response.setCreatedDate(customer.getCreatedDate());

            return response;
        }).toList();
    }

    public CustomerResponseDto getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerResponseDto response = new CustomerResponseDto();

        response.setCustomerId(customer.getCustomerId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setMobile(customer.getMobile());
        response.setAddress(customer.getAddress());
        response.setCreatedDate(customer.getCreatedDate());

        return response;
    }

    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto requestDto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(requestDto.getName());
        customer.setEmail(requestDto.getEmail());
        customer.setMobile(requestDto.getMobile());
        customer.setAddress(requestDto.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);

        return new CustomerResponseDto(
                updatedCustomer.getCustomerId(),
                updatedCustomer.getName(),
                updatedCustomer.getEmail(),
                updatedCustomer.getMobile(),
                updatedCustomer.getAddress(),
                updatedCustomer.getCreatedDate()
        );
    }

    public void deleteCustomer(Long id) {

        if(!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(
                    "Customer not found with id : " + id
            );
        }

        customerRepository.deleteById(id);
    }

}
