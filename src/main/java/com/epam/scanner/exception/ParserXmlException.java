package com.epam.scanner.exception;

public class ParserXmlException extends Exception {

	private static final long serialVersionUID = -1102944275630398310L;


	public ParserXmlException() {
		super();

	}

	public ParserXmlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	
	}


	public ParserXmlException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ParserXmlException(String message) {
		super(message);

	}


	public ParserXmlException(Throwable cause) {
		super(cause);
	
	}
}
