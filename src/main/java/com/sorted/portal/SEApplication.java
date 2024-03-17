package com.sorted.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.sorted.portal")
public class SEApplication {

	public static void main(String[] args) {
		SpringApplication.run(SEApplication.class, args);
	}

}
