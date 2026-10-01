package com.booking.service;

import com.booking.dto.UserResponse;
import com.booking.model.User;
import com.booking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream()
				.map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole().name())).toList();
	}

	public UserResponse createUser(String name, String email, String password, String role) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("Email already registered");
		}
		User user = User.builder().name(name).email(email).password(passwordEncoder.encode(password))
				.role(User.Role.valueOf(role.toUpperCase())).build();
		User saved = userRepository.save(user);
		return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole().name());
	}

	public UserResponse updateRole(Long userId, String role) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		user.setRole(User.Role.valueOf(role.toUpperCase()));
		User saved = userRepository.save(user);
		return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole().name());
	}

	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);
	}
}