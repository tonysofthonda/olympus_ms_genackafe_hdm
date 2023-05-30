package com.honda.olympus.exception;

public class FileProcessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3301101565847251974L;

	public FileProcessException(String message) {
		super(message);
	}

	public FileProcessException(Throwable ex) {
		super(ex);
	}

	public FileProcessException(String message, Throwable ex) {
		super(message, ex);
	}

}
