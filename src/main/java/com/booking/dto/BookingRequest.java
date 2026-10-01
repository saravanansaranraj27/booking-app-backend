package com.booking.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(@NotNull(message = "Listing ID is required") Long listingId) {
}