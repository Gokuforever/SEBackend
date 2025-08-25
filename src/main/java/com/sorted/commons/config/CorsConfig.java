package com.sorted.commons.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Configuration
public class CorsConfig {

	@Value("${cors.allowed.origins}")
	private String allowedOrigins;

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		List<String> domains = Arrays.asList(allowedOrigins.split(","));
		log.info("Allowed origins: {}", domains);

		// Set allowed origins
		config.setAllowedOrigins(domains);

		// CRITICAL: Add origin patterns for Vercel deployments
		config.addAllowedOriginPattern("https://*.vercel.app");
		config.addAllowedOriginPattern("https://*-anand-suryawanshis-projects.vercel.app");

		// Allow all HTTP methods (IMPORTANT: Include OPTIONS)
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("PATCH");

		// Allow all headers
		config.addAllowedHeader("*");

		// Allow credentials
		config.setAllowCredentials(true);

		// Set max age for preflight cache
		config.setMaxAge(3600L);

		// Expose headers that frontend might need
		config.addExposedHeader("*");

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}