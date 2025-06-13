package org.ua.drmp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI drmpOpenAPI() {
		String schemeName = "bearerAuth";

		return new OpenAPI()
			.info(new Info()
				.title("DRMP API")
				.version("1.0.0")
				.description("API Documentation for DRMP"))
			.addSecurityItem(new SecurityRequirement()
				.addList(schemeName))
			.components(new Components()
				.addSecuritySchemes(schemeName,
					new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.in(SecurityScheme.In.HEADER)
						.name("Authorization")));
	}
}
