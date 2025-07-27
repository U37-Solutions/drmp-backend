package org.ua.drmp.company.dictionary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IdNameDto(@NotNull(message = "ID must not be null") Long id,
	@NotBlank(message = "Name must not be blank") String name) {
}
