package com.secure.confirg;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.secure.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	
	public SecurityConfig(JwtAuthenticationFilter authenticationFilter) {
		this.authenticationFilter = authenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
	        throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())

	        .cors(cors -> {})
	        
	        .sessionManagement(session ->
            session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
            )
        )
	        .authorizeHttpRequests(auth -> auth
	        		
	        		.requestMatchers(
	        		        "/swagger-ui/**",
	        		        "/swagger-ui.html",
	        		        "/v3/api-docs/**"
	        		).permitAll()
	        		 
	                .requestMatchers(
	                        "/auth/register",
	                        "/auth/login",
	                        "/auth/refresh",
	                        "/auth/logout"
	                ).permitAll()

	                .requestMatchers("/auth/change-password").authenticated()

	                .requestMatchers("/employees/admin/**").hasRole("ADMIN")
	                .requestMatchers("/employees/**").hasAnyRole("USER", "ADMIN")

	                .anyRequest().authenticated()
	        )
	        .exceptionHandling(ex -> ex
	                .authenticationEntryPoint((request, response, authException) -> {

	                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                    response.setContentType("application/json");

	                    response.getWriter().write(
	                            "{\"error\":\"Unauthorized\",\"message\":\"Authentication required or token is invalid\"}"
	                    );
	                })
	                .accessDeniedHandler((request, response, accessDeniedException) -> {

	                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	                    response.setContentType("application/json");

	                    response.getWriter().write(
	                            "{\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource\"}"
	                    );
	                })
	        )
	        .addFilterBefore(
	                authenticationFilter,
	                UsernamePasswordAuthenticationFilter.class
	        );

	    return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration authenticationConfiguration)throws Exception
	{
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

	    CorsConfiguration configuration = new CorsConfiguration();

	    configuration.setAllowedOrigins(
	    		List.of(
	                    "http://localhost:5174",
	                    "https://secureemployeemanagementfrontend.onrender.com"
	            )
	    );

	    configuration.setAllowedMethods(
	            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
	    );

	    configuration.setAllowedHeaders(
	            List.of("*")
	    );

	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();

	    source.registerCorsConfiguration("/**", configuration);

	    return source;
	}

}
