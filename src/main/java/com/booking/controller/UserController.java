package com.booking.controller;

import com.booking.dto.UpdateRoleRequest;
import com.booking.dto.UserResponse;
import com.booking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<UserResponse> getAllUsers() {
		return userService.getAllUsers();
	}

	@PostMapping
	public UserResponse createUser(@RequestBody Map<String, String> body) {
		return userService.createUser(body.get("name"), body.get("email"), body.get("password"), body.get("role"));
	}

	@PutMapping("/{id}/role")
	public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest req) {
		return userService.updateRole(id, req.role());
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
}