package com.secure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secure.dto.AuthResponse;
import com.secure.dto.ChangePasswordRequest;
import com.secure.dto.LoginRequest;
import com.secure.dto.RefreshRequest;
import com.secure.dto.RegisterRequest;
import com.secure.service.AuthService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request)
	{
		return ResponseEntity.ok(authService.register(request));
	}
	

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
	    return authService.login(request);
	}
	
	@PostMapping("/refresh")
	public AuthResponse refresh(@RequestBody RefreshRequest request) {

	    return authService.refreshAccessToken(
	            request.getRefreshToken()
	    );
	}
	
	@PostMapping("/logout")
	public String logout(@RequestBody RefreshRequest request) {

	    return authService.logout(
	            request.getRefreshToken()
	    );
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(
	        @Valid @RequestBody ChangePasswordRequest request,
	        Authentication authentication) {

	    String username = authentication.getName();

	    return ResponseEntity.ok(
	            authService.changePassword(
	                    username,
	                    request.getOldPassword(),
	                    request.getNewPassword()
	            )
	    );
	}
	

}
