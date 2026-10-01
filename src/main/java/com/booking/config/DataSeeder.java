package com.booking.config;

import com.booking.model.Listing;
import com.booking.model.User;
import com.booking.repository.ListingRepository;
import com.booking.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

	@Value("${admin.email}")
	private String adminEmail;

	@Value("${admin.password}")
	private String adminPassword;

	@Bean
	CommandLineRunner seedData(UserRepository userRepository, ListingRepository listingRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByEmail(adminEmail).isEmpty()) {
				User admin = User.builder().name("Admin").email(adminEmail)
						.password(passwordEncoder.encode(adminPassword)).role(User.Role.ADMIN).build();
				userRepository.save(admin);
				System.out.println("✅ Admin created: " + adminEmail);
			}
			if (listingRepository.count() == 0) {
				List<Listing> listings = List.of(
						Listing.builder().title("React Masterclass")
								.description("Learn React from scratch with hands-on projects.").category("workshop")
								.price(49.99).date(LocalDate.of(2026, 9, 5)).slotsAvailable(30).build(),
						Listing.builder().title("Tech Conference 2026")
								.description("Annual technology conference with industry leaders.")
								.category("conference").price(199.99).date(LocalDate.of(2026, 9, 15))
								.slotsAvailable(100).build(),
						Listing.builder().title("Jazz Night Live")
								.description("Smooth jazz performances by local artists.").category("concert")
								.price(35.00).date(LocalDate.of(2026, 9, 8)).slotsAvailable(50).build(),
						Listing.builder().title("Python for Data Science")
								.description("Hands-on pandas and ML basics workshop.").category("workshop")
								.price(59.99).date(LocalDate.of(2026, 9, 12)).slotsAvailable(25).build(),
						Listing.builder().title("Startup Pitch Night")
								.description("Startups pitch to investors and mentors.").category("conference")
								.price(0.00).date(LocalDate.of(2026, 9, 20)).slotsAvailable(80).build(),
						Listing.builder().title("Acoustic Sunset Session")
								.description("Outdoor acoustic music with food trucks.").category("concert")
								.price(25.00).date(LocalDate.of(2026, 9, 3)).slotsAvailable(40).build());
				listingRepository.saveAll(listings);
				System.out.println("✅ Seeded " + listings.size() + " listings");
			}
		};
	}
}