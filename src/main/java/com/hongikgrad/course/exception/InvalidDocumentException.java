package com.hongikgrad.course.exception;

public class InvalidDocumentException extends Exception {
	public InvalidDocumentException() {
	}
	public InvalidDocumentException(String message) {
		super(message);
	}
	public InvalidDocumentException(String message, Throwable cause) {
		super(message, cause);
	}
}
