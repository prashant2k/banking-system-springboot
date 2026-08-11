package com.prashant.bank.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "password is required")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean active = true;
}