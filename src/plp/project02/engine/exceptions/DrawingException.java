package plp.project02.engine.exceptions;

public class DrawingException extends Exception {
	private static final long serialVersionUID = 8604674928201811293L;
	
	public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_IMAGE_FRAME = -1;
	public static final int ERROR_FRAME_BITMAP = -2;
	public static final int ERROR_CANVAS = -3;
	public static final int ERROR_IMAGE_NULL = -4;
	
	private int errorCode;
	
	public DrawingException() {
		super();
		errorCode = ERROR_UNKNOWN;
	}
	
	public DrawingException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}
	
	public String getErrorMsg() {
		String errorDesc;
		
		switch(errorCode) {
		case ERROR_IMAGE_FRAME:
			errorDesc = "The image frame you tried to draw does not exist.";
			break;
		case ERROR_FRAME_BITMAP:
			errorDesc = "Android could not create a bitmap for the image frame you want to draw.";
			break;
		case ERROR_CANVAS:
			errorDesc = "The Android's Canvas object was not created.";
			break;
		case ERROR_IMAGE_NULL:
			errorDesc = "The image is null.";
			break;
		case ERROR_UNKNOWN:
		default:
			errorDesc = "Unknown error.";
		}
		
		return "Unable to draw: " + errorDesc;
	}

}
