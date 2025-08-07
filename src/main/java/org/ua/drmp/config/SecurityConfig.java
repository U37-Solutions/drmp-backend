package org.ua.drmp.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;
	private final UserDetailsService userDetailsService;

	private static final String[] SWAGGER_WHITELIST = {
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/v3/api-docs/**",
		"/api-docs/**"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SWAGGER_WHITELIST).permitAll()
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers("/auth/refresh").permitAll()
				.requestMatchers("/temporary/**").permitAll()
				.requestMatchers("/confirm-registration").permitAll()

				.requestMatchers(HttpMethod.POST, "/chat/start").permitAll()

				.requestMatchers(HttpMethod.GET, "/custom-structures").permitAll()
				.requestMatchers(HttpMethod.GET, "/dictionary/services").permitAll()
				.requestMatchers(HttpMethod.GET, "/dictionary/categories").permitAll()
				.requestMatchers(HttpMethod.GET, "/dictionary/conditions").permitAll()
				.requestMatchers(HttpMethod.GET, "/dictionary/company-types").permitAll()
				.requestMatchers(HttpMethod.GET, "/dictionary/cities/**").permitAll()

				.requestMatchers(HttpMethod.GET, "/public/companies").permitAll()
				.requestMatchers(HttpMethod.GET, "/public/map-points").permitAll()

				.requestMatchers(HttpMethod.GET, "/chat/history").permitAll()

				.requestMatchers(HttpMethod.POST, "/feedbacks").permitAll()

				.requestMatchers(HttpMethod.POST, "/company-register").permitAll()
				.requestMatchers("/ws/**").permitAll()

				.anyRequest().authenticated()
			)
			.userDetailsService(userDetailsService)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// TODO: before release please remove incorrect urls
		configuration.setAllowedOrigins(
			List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:8080", "https://drmp.u37solutions.com", "https://admin-drmp.u37solutions.com", "https://api-drmp.u37solutions.com"));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		// if in future I will set tokens in header, will need
		// configuration.setExposedHeaders(List.of("Authorization"));

		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Chat-Token"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
