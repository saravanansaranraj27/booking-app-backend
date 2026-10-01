package com.booking.controller;

import com.booking.model.Listing;
import com.booking.service.ListingService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Valid
@RequestMapping("/listings")
public class ListingController {

	private final ListingService listingService;

	public ListingController(ListingService listingService) {
		this.listingService = listingService;
	}

	@GetMapping
	public List<Listing> getAll(@RequestParam(required = false) String category) {
		return category == null ? listingService.getAll() : listingService.getByCategory(category);
	}

	@GetMapping("/{id}")
	public Listing getOne(@PathVariable Long id) {
		return listingService.getById(id);
	}

	@PostMapping
	public Listing create(@RequestBody Listing listing) {
		return listingService.create(listing);
	}

	@PutMapping("/{id}")
	public Listing update(@PathVariable Long id, @RequestBody Listing listing) {
		return listingService.update(id, listing);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		listingService.delete(id);
	}
}