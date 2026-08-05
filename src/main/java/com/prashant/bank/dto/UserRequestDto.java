package com.prashant.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequestDto {

    @NotBlank(message="name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message="invalid email format")
    private String email;


}
