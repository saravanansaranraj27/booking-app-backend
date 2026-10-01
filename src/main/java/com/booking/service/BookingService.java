package com.booking.service;

import com.booking.model.Booking;
import com.booking.model.Listing;
import com.booking.model.User;
import com.booking.repository.BookingRepository;
import com.booking.repository.ListingRepository;
import com.booking.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	private final ListingRepository listingRepository;
	private final UserRepository userRepository;

	public BookingService(BookingRepository bookingRepository, ListingRepository listingRepository,
			UserRepository userRepository) {
		this.bookingRepository = bookingRepository;
		this.listingRepository = listingRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public Booking createBooking(Long listingId, String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
		Listing listing = listingRepository.findById(listingId)
				.orElseThrow(() -> new RuntimeException("Listing not found"));

		if (bookingRepository.existsByListingIdAndUserId(listingId, user.getId())) {
			throw new RuntimeException("You have already booked this listing");
		}

		if (listing.getSlotsAvailable() <= 0) {
			throw new RuntimeException("No slots available for this event");
		}

		listing.setSlotsAvailable(listing.getSlotsAvailable() - 1);
		listingRepository.save(listing);

		Booking booking = Booking.builder().user(user).listing(listing).status(Booking.Status.CONFIRMED)
				.createdAt(LocalDateTime.now()).build();

		return bookingRepository.save(booking);
	}

	public List<Booking> getUserBookings(String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
		return bookingRepository.findByUser(user);
	}

	@Transactional
	public void cancelBooking(Long bookingId, String userEmail) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		if (!booking.getUser().getEmail().equals(userEmail)) {
			throw new RuntimeException("Not authorized");
		}
		booking.setStatus(Booking.Status.CANCELLED);
		Listing listing = booking.getListing();
		listing.setSlotsAvailable(listing.getSlotsAvailable() + 1);
		listingRepository.save(listing);
		bookingRepository.save(booking);
	}
}