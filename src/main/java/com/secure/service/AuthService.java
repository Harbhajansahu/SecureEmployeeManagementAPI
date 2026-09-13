package com.secure.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.secure.dto.AuthResponse;
import com.secure.dto.LoginRequest;
import com.secure.dto.RegisterRequest;
import com.secure.entity.AppUser;
import com.secure.entity.RefreshToken;
import com.secure.exception.DuplicateUsernameException;
import com.secure.exception.InvalidTokenException;
import com.secure.exception.UserNotFoundException;
import com.secure.repositry.RefreshTokenRepository;
import com.secure.repositry.UserRepository;
import com.secure.security.JwtService;

@Service
public class AuthService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final RefreshTokenRepository tokenRepository;
	private final UserDetailsService userDetailsService;

	public AuthService(UserRepository repository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenRepository tokenRepository,
			UserDetailsService userDetailsService) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.tokenRepository = tokenRepository;
		this.userDetailsService = userDetailsService;
	}

	public String register(RegisterRequest request) {

		if (repository.findByUsername(request.getUsername()).isPresent()) {
			throw new DuplicateUsernameException("Username already exists");
		}

		AppUser user = new AppUser();

		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("USER");

		repository.save(user);

		return "User Register Successfully..!";
	}

	public AuthResponse login(LoginRequest request) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

		String accessToken = jwtService.generateToken(authentication.getName(), role);

		String refreshToken = jwtService.generateRefreshToken(authentication.getName());

		RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, authentication.getName(), false);
		tokenRepository.save(refreshTokenEntity);

		return new AuthResponse(accessToken, refreshToken);
	}

	public AuthResponse refreshAccessToken(String refreshToken) {

		if (!jwtService.isRefreshToken(refreshToken)) {
			throw new InvalidTokenException("Invalid refresh token");
		}

		RefreshToken storedToken = tokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

		if (storedToken.isRevoked()) {
			throw new InvalidTokenException("Refresh token already revoked");
		}

		if (!jwtService.isTokenValid(refreshToken)) {
			throw new InvalidTokenException("Refresh token expired");
		}

		String username = jwtService.extractUsername(refreshToken);

		// User ko DB se load karo
		var userDetails = userDetailsService.loadUserByUsername(username);

		// User ka current role nikalo
		String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

		// Old refresh token revoke
		storedToken.setRevoked(true);
		tokenRepository.save(storedToken);

		// New access token with role
		String newAccessToken = jwtService.generateToken(username, role);

		// New refresh token
		String newRefreshToken = jwtService.generateRefreshToken(username);

		RefreshToken newToken = new RefreshToken(newRefreshToken, username, false);

		tokenRepository.save(newToken);

		return new AuthResponse(newAccessToken, newRefreshToken);
	}

	public String logout(String refreshToken) {

	    if (!jwtService.isRefreshToken(refreshToken)) {
	        throw new InvalidTokenException("Invalid refresh token");
	    }

	    RefreshToken storedToken = tokenRepository.findByToken(refreshToken)
	            .orElseThrow(() ->
	                new InvalidTokenException("Refresh token not found"));

	    if (storedToken.isRevoked()) {
	        throw new InvalidTokenException("Refresh token already revoked");
	    }

	    storedToken.setRevoked(true);
	    tokenRepository.save(storedToken);

	    return "Logout successful";
	}

	public String changePassword(String username, String oldPassword, String newPassword) {

		AppUser user = repository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));

		// Check old password
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new BadCredentialsException("Old password is incorrect");
		}

		// Encode new password
		user.setPassword(passwordEncoder.encode(newPassword));
		repository.save(user);

		// Revoke all refresh tokens
		var tokens = tokenRepository.findAllByUsername(username);

		for (RefreshToken token : tokens) {
			token.setRevoked(true);
		}

		tokenRepository.saveAll(tokens);

		return "Password changed successfully";
	}
}
