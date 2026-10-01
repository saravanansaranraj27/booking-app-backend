package com.booking.controller;

import com.booking.dto.BookingRequest;
import com.booking.model.Booking;
import com.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping
	public Booking create(@Valid @RequestBody BookingRequest req, Authentication auth) {
		return bookingService.createBooking(req.listingId(), auth.getName());
	}

	@GetMapping("/my")
	public List<Booking> myBookings(Authentication auth) {
		return bookingService.getUserBookings(auth.getName());
	}

	@DeleteMapping("/{id}")
	public void cancel(@PathVariable Long id, Authentication auth) {
		bookingService.cancelBooking(id, auth.getName());
	}
}