package plp.project02.engine.exceptions;

public class ImageNotLoadedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_FILE_NOT_FOUND = -1;
	public static final int ERROR_BITMAP_NOT_CRATED = -2;
	
	private String imageFilepath;
	private int errorCode;
	
	public ImageNotLoadedException() {
		super();
		imageFilepath = "Unknown image";
		errorCode = ERROR_UNKNOWN;
	}
	
	public ImageNotLoadedException(String imageFilePath, int errorCode) {
		super();
		this.imageFilepath = imageFilePath;
		this.errorCode = errorCode;
	}
	
	public String getErrorMsg() {
		String errorDesc;
		
		switch (errorCode) {
		case ERROR_FILE_NOT_FOUND:
			errorDesc = "Image file not found.";
			break;
		case ERROR_BITMAP_NOT_CRATED:
			errorDesc = "Android could not create image bitmap.";
			break;
		case ERROR_UNKNOWN:
		default:
			errorDesc = "Unknown error.";
		}
		
		return "The image " + imageFilepath + " could not be loaded: " + errorDesc;
	}
}
