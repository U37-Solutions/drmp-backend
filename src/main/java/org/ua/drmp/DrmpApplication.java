package org.ua.drmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DrmpApplication {

	public static void main(String[] args) {
    String dbUrl = System.getenv("DB_HOST");
    System.out.println("DB_HOST = " + dbUrl);
    if (dbUrl == null) {
        System.err.println("⚠️ DB_HOST is null!");
    }

		SpringApplication.run(DrmpApplication.class, args);
	}

}
