package com.prashant.bank.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prashant.bank.customer.dto.CustomerRequestDto;
import com.prashant.bank.customer.dto.CustomerResponseDto;
import com.prashant.bank.customer.service.CustomerService;
import com.prashant.bank.security.filter.JwtAuthenticationFilter;
import com.prashant.bank.security.service.CustomUserDetailsService;
import com.prashant.bank.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CustomerController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtAuthenticationFilter.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================================================
    // CREATE CUSTOMER
    // =========================================================

    @Test
    void shouldCreateCustomer() throws Exception {

        CustomerRequestDto request = new CustomerRequestDto(
                "Prashant Mishra",
                "prashant@test.com",
                "9876543210",
                "Noida"
        );

        CustomerResponseDto response = new CustomerResponseDto(
                1L,
                "Prashant Mishra",
                "prashant@test.com",
                "9876543210",
                "Noida",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(customerService.createCustomer(any(CustomerRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.name").value("Prashant Mishra"))
                .andExpect(jsonPath("$.email").value("prashant@test.com"))
                .andExpect(jsonPath("$.mobile").value("9876543210"))
                .andExpect(jsonPath("$.address").value("Noida"))
                .andExpect(jsonPath("$.active").value(true));

        verify(customerService)
                .createCustomer(any(CustomerRequestDto.class));
    }

    // =========================================================
    // GET ALL CUSTOMERS
    // =========================================================

    @Test
    void shouldGetAllCustomers() throws Exception {

        CustomerResponseDto response = new CustomerResponseDto(
                1L,
                "Prashant Mishra",
                "prashant@test.com",
                "9876543210",
                "Noida",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(customerService.getAllCustomers())
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/v1/customers")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].name").value("Prashant Mishra"))
                .andExpect(jsonPath("$[0].email").value("prashant@test.com"))
                .andExpect(jsonPath("$[0].mobile").value("9876543210"))
                .andExpect(jsonPath("$[0].address").value("Noida"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(customerService)
                .getAllCustomers();
    }

    // =========================================================
    // GET CUSTOMER BY ID
    // =========================================================

    @Test
    void shouldGetCustomerById() throws Exception {

        CustomerResponseDto response = new CustomerResponseDto(
                1L,
                "Prashant Mishra",
                "prashant@test.com",
                "9876543210",
                "Noida",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(customerService.getCustomerById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/customers/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.name").value("Prashant Mishra"))
                .andExpect(jsonPath("$.email").value("prashant@test.com"))
                .andExpect(jsonPath("$.mobile").value("9876543210"))
                .andExpect(jsonPath("$.active").value(true));

        verify(customerService)
                .getCustomerById(1L);
    }

    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    @Test
    void shouldUpdateCustomer() throws Exception {

        CustomerRequestDto request = new CustomerRequestDto(
                "Updated Customer",
                "updated@test.com",
                "9876543210",
                "Greater Noida"
        );

        CustomerResponseDto response = new CustomerResponseDto(
                1L,
                "Updated Customer",
                "updated@test.com",
                "9876543210",
                "Greater Noida",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(customerService.updateCustomer(
                eq(1L),
                any(CustomerRequestDto.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.name").value("Updated Customer"))
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.mobile").value("9876543210"))
                .andExpect(jsonPath("$.active").value(true));

        verify(customerService)
                .updateCustomer(
                        eq(1L),
                        any(CustomerRequestDto.class)
                );
    }

    // =========================================================
    // DELETE CUSTOMER
    // =========================================================

    @Test
    void shouldDeleteCustomer() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/customers/1")
                )
                .andExpect(status().isNoContent());

        verify(customerService)
                .deleteCustomer(1L);
    }
}