package edu.wisc.doit.tcrypt.exception;

public class ServiceErrorException extends ValidationException {

	public ServiceErrorException(String serviceName, String errorMessage) {
		super(errorMessage);
		this.serviceName = serviceName;
	}

	private static final long serialVersionUID = -2269068332568805521L;

	private String serviceName;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
}
