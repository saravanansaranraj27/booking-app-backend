package com.booking.dto;

public record AuthResponse(String token, String role, String name) {
}