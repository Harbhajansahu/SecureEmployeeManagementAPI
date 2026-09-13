package com.secure.exception;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	
	@ExceptionHandler(InvalidTokenException.class)
	 public ResponseEntity<String> handleInvalidToken(InvalidTokenException ex)
	 {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				 .body(ex.getMessage());
	 }
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(
	        MethodArgumentNotValidException ex) {

	    Map<String, String> errors = new HashMap<>();

	    ex.getBindingResult()
	            .getFieldErrors()
	            .forEach(error ->
	                errors.put(
	                    error.getField(),
	                    error.getDefaultMessage()
	                )
	            );

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(errors);
	}
	
	@ExceptionHandler(DuplicateUsernameException.class)
	public ResponseEntity<String> handleDuplicateUsername(
	        DuplicateUsernameException ex) {

	    return ResponseEntity
	            .status(HttpStatus.CONFLICT)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentials(
	        BadCredentialsException ex) {

	    return ResponseEntity
	            .status(HttpStatus.UNAUTHORIZED)
	            .body("Invalid username or password");
	}
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFound(
	        UserNotFoundException ex) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(ex.getMessage());
	}
 }
