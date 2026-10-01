package com.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message = "Name is required") String name,
		@Email(message = "Invalid email format") String email,
		@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password) {
}