package org.ua.drmp.exception;

public class AuthorizationHeaderMissingException extends RuntimeException {
	public AuthorizationHeaderMissingException(String message) {
		super(message);
	}
}
