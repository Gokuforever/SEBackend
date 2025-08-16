package com.sorted.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("https://stz-frontend-service-ts.vercel.app", // Vercel frontend
				"http://localhost:5173", // Local development
				"https://vinayak.studeaze.in",
				"https://seller.studeaze.in",
				"https://studeaze.in",
				"https://www.studeaze.in/",
				"https://gokuforever.github.io",
				"https://studeaze.retool.com",
				"https://stz-frontend-service-ts-git-dev-anand-suryawanshis-projects.vercel.app"
		));

		config.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, etc.)
		config.addAllowedHeader("*"); // Allow all headers
		config.setAllowCredentials(true); // Allow credentials if needed (for cookies, etc.)

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
