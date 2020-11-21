package org.malibu.ibanez.dao;

public class IbanezException extends Exception {

	private static final long serialVersionUID = 5274907233310632825L;

	public IbanezException() {
		super();
	}

	public IbanezException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public IbanezException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IbanezException(String message) {
		super(message);
	}

	public IbanezException(Throwable cause) {
		super(cause);
	}

}
