package org.ua.drmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DrmpApplication {

    @Value("${DB_URL}")
    private String dbUrl;

    @PostConstruct
    public void logEnv() {
        System.out.println("DB_URL = " + dbUrl);
        if (dbUrl == null) {
            System.err.println("⚠️ DB_URL is null!");
        }
    }


	public static void main(String[] args) {
		SpringApplication.run(DrmpApplication.class, args);
	}

}
