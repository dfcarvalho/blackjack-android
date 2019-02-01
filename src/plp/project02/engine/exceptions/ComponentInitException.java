package plp.project02.engine.exceptions;

public class ComponentInitException extends Exception {
	private static final long serialVersionUID = -4241827264744004584L;

	public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_VIDEO_INIT = -1;
	
	private int errorCode;
	
	public ComponentInitException() {
		super();
		errorCode = ERROR_UNKNOWN;
	}
	
	public ComponentInitException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}
	
	public String getErrorMsg() {
		String errorDesc;
		
		switch(errorCode) {
		case ERROR_VIDEO_INIT:
			errorDesc = "Unable to initialize video manager.";
			break;
		case ERROR_UNKNOWN:
		default:
			errorDesc = "Unknown error.";
			break;
		}
		
		return "Initialization Error: " + errorDesc;
	}
}
