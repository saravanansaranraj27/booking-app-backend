package com.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", uniqueConstraints = @UniqueConstraint(columnNames = { "listing_id", "user_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnoreProperties({ "password", "email" })
	private User user;

	@ManyToOne
	@JoinColumn(name = "listing_id")
	private Listing listing;

	@Enumerated(EnumType.STRING)
	private Status status;

	private LocalDateTime createdAt;

	public enum Status {
		CONFIRMED, CANCELLED
	}
}