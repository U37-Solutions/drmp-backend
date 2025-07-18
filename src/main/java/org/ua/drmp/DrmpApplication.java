package org.ua.drmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DrmpApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrmpApplication.class, args);
	}

}
