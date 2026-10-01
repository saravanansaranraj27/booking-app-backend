package com.booking.repository;

import com.booking.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
	java.util.List<Listing> findByCategoryIgnoreCase(String category);
}