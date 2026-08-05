package com.prashant.bank.customer.repository;

import com.prashant.bank.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByMobile(String mobile);
}
