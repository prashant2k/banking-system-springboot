package com.prashant.bank.customer.controller;

import com.prashant.bank.customer.dto.CustomerRequestDto;
import com.prashant.bank.customer.dto.CustomerResponseDto;
import com.prashant.bank.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public CustomerResponseDto createCustomer(@Valid @RequestBody CustomerRequestDto request){
        return customerService.createCustomer(request);
    }

    @GetMapping
    public List<CustomerResponseDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerResponseDto getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponseDto updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDto requestDto) {

        return customerService.updateCustomer(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {

        customerService.deleteCustomer(id);

        return "Customer deleted successfully";
    }
}
