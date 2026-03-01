package com.passly.auth.dto;

import com.passly.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank @Email
    String email,

    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotNull
    UserRole role,

    @NotBlank @Size(max = 100)
    String firstName,

    @NotBlank @Size(max = 100)
    String lastName
) {}
