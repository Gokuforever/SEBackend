package com.sorted.commons.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// Exact origins - no wildcards when using credentials
		config.setAllowedOrigins(Arrays.asList(
				"https://stz-frontend-service-ts.vercel.app",
				"http://localhost:5173",
				"https://vinayak.studeaze.in",
				"https://seller.studeaze.in",
				"https://gokuforever.github.io",
				"https://studeaze.retool.com"
		));

		// Allow all methods
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

		// Allow all headers (this should work with credentials)
		config.addAllowedHeader("*");

		// Allow credentials
		config.setAllowCredentials(true);

		// Set preflight max age
		config.setMaxAge(3600L);

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}