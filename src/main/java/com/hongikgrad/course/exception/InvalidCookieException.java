package com.hongikgrad.course.exception;

import lombok.NoArgsConstructor;

public class InvalidCookieException extends Exception {
	public InvalidCookieException() {
	}

	public InvalidCookieException(String message) {
		super(message);
	}

	public InvalidCookieException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCookieException(Throwable cause) {
		super(cause);
	}

	public InvalidCookieException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
