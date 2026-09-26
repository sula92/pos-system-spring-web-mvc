package com.pos.exception;

// Thrown when the client sends bad or incomplete input data.
// This is an unchecked exception because validation failures are handled centrally.
public class InvalidRequestException extends RuntimeException {

	public InvalidRequestException(String message) {
		// Keep the original message so the global handler can return it to the client.
		super(message);
	}
}

