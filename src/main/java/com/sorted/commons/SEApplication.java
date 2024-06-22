package com.sorted.commons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.sorted.commons")
public class SEApplication {

	public static void main(String[] args) {
		SpringApplication.run(SEApplication.class, args);
	}

}
