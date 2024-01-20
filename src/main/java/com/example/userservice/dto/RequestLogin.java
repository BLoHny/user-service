package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestLogin {

    @Email
    @NotNull(message = "email cannot be null")
    private String email;

    @Size(min = 8, message = "password must be equals or grater than 8 characters")
    @NotNull(message = "password cannot be null")
    private String password;
}
