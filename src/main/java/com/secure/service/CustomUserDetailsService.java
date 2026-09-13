package com.secure.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import com.secure.entity.AppUser;
import com.secure.repositry.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository repository;

	public CustomUserDetailsService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		AppUser user = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return User.withUsername(user.getUsername())
				.password(user.getPassword())
				.roles(user.getRole()).build();
	}

}
