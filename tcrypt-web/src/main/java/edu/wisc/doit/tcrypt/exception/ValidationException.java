package edu.wisc.doit.tcrypt.exception;

public class ValidationException extends Exception {

	private static final long serialVersionUID = -8739113115494373365L;
	private String errorMessage;
	
	public ValidationException(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
