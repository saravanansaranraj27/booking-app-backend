package com.booking.controller;

import com.booking.dto.*;
import com.booking.model.User;
import com.booking.repository.UserRepository;
import com.booking.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
		if (userRepository.findByEmail(req.email()).isPresent()) {
			return ResponseEntity.badRequest().body("Email already registered");
		}
		User user = User.builder().name(req.name()).email(req.email()).password(passwordEncoder.encode(req.password()))
				.role(User.Role.USER).build();
		userRepository.save(user);
		String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
		return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), user.getName()));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
		User user = userRepository.findByEmail(req.email()).orElse(null);
		if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
			return ResponseEntity.status(401).body("Invalid credentials");
		}
		String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
		return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), user.getName()));
	}
}