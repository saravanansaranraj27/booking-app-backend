package com.booking.service;

import com.booking.model.Listing;
import com.booking.repository.ListingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListingService {

	private final ListingRepository listingRepository;

	public ListingService(ListingRepository listingRepository) {
		this.listingRepository = listingRepository;
	}

	public List<Listing> getAll() {
		return listingRepository.findAll();
	}

	public List<Listing> getByCategory(String category) {
		return listingRepository.findByCategoryIgnoreCase(category);
	}

	public Listing getById(Long id) {
		return listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
	}

	public Listing create(Listing listing) {
		return listingRepository.save(listing);
	}

	public Listing update(Long id, Listing updated) {
		Listing existing = getById(id);
		existing.setTitle(updated.getTitle());
		existing.setDescription(updated.getDescription());
		existing.setCategory(updated.getCategory());
		existing.setPrice(updated.getPrice());
		existing.setDate(updated.getDate());
		existing.setSlotsAvailable(updated.getSlotsAvailable());
		return listingRepository.save(existing);
	}

	public void delete(Long id) {
		listingRepository.deleteById(id);
	}
}