package com.secure.repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	Optional<RefreshToken>  findByToken(String  token);
	 Optional<RefreshToken> findByUsername(String username);
	 
	 List<RefreshToken> findAllByUsername(String username);

}
