package org.ua.drmp.swagger.annotation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses(value = {
	@ApiResponse(responseCode = "401", description = "Unauthorized. Causes: Invalid or expired JWT token, Invalid refresh token, Email not found in token, Refresh token is not valid, User not found")
})
public @interface ApiError401 {
}
