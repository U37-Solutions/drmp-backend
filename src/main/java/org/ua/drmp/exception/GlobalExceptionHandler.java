package org.ua.drmp.exception;

import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ForbiddenOperationException.class)
	public ResponseEntity<Object> handleForbiddenOperation(ForbiddenOperationException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.FORBIDDEN);
	}
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthorizationHeaderMissingException.class)
	public ResponseEntity<Object> handleMissingAuthHeader(AuthorizationHeaderMissingException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<Object> handleInvalidPassword(InvalidPasswordException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneric(Exception ex) {
		return new ResponseEntity<>(Map.of("error", "Unexpected error: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidJwtException.class)
	public ResponseEntity<Object> handleInvalidJwt(InvalidJwtException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.UNAUTHORIZED);
	}
	@ExceptionHandler(TokenValidationException.class)
	public ResponseEntity<Object> handleTokenValidation(TokenValidationException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	public ResponseEntity<Object> handleEmailAlreadyInUse(EmailAlreadyInUseException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		String message = extractRootCauseMessage(ex);
		return buildErrorResponse("Data integrity error: " + message);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
		String message = ex.getSQLException() != null ? ex.getSQLException().getMessage() : ex.getMessage();
		return buildErrorResponse("Constraint violation: " + message);
	}

	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<Map<String, String>> handlePersistence(PersistenceException ex) {
		return buildErrorResponse("Persistence error: " + extractRootCauseMessage(ex));
	}

	@ExceptionHandler(SQLException.class)
	public ResponseEntity<Map<String, String>> handleSqlException(SQLException ex) {
		return buildErrorResponse("SQL error: " + ex.getMessage());
	}

	@ExceptionHandler(BadSqlGrammarException.class)
	public ResponseEntity<Map<String, String>> handleBadSqlGrammar(BadSqlGrammarException ex) {
		return buildErrorResponse("Bad SQL syntax: " + ex.getMessage());
	}

	private ResponseEntity<Map<String, String>> buildErrorResponse(String message) {
		Map<String, String> errorBody = new HashMap<>();
		errorBody.put("error", message);
		return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
	}

	private String extractRootCauseMessage(Throwable ex) {
		Throwable root = ex;
		while (root.getCause() != null) {
			root = root.getCause();
		}
		return root.getMessage();
	}
}
