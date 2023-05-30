package com.honda.olympus.exception;

public class GenackafeException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5188340524260570979L;

	public GenackafeException(String message) {
		super(message);
	}

	public GenackafeException(Throwable ex) {
		super(ex);
	}

	public GenackafeException(String message, Throwable ex) {
		super(message, ex);
	}

}
