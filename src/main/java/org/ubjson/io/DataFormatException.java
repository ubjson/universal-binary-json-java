package org.ubjson.io;

public class DataFormatException extends RuntimeException {
	private static final long serialVersionUID = 4874357982561267152L;

	public DataFormatException(String reason) {
		super(reason);
	}
}