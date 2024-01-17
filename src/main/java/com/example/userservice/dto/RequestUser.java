package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestUser {

    @Email(message = "Invalid Email Type")
    @NotNull(message = "Email Is Necessary Value")
    private String email;

    @Size(min = 2, message = "Name not be less than two characters")
    @NotNull(message = "Name Is Necessary Value")
    private String name;

    @Size(min = 8, message = "Password not be less than two characters")
    @NotNull(message = "Password Is Necessary Value")
    private String pwd;
}
