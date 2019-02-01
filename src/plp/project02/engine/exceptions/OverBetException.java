package plp.project02.engine.exceptions;

public class OverBetException extends Exception {
	private static final long serialVersionUID = 8514648712445922236L;
	
	public static final int ERROR_UNKOWN = 0;
	public static final int BET_MORE_THAN_CASH = 1;
	public static final int BET_MORE_THAN_MAX_BET = 2;
	
	private int errorCode;
	
	public OverBetException() {
		super();
		errorCode = ERROR_UNKOWN;
	}
	
	public OverBetException(int code) {
		super();
		this.errorCode = code;
	}
	
	public String getErrorMsg() {
		String errorDesc;
		
		switch (errorCode) {
		case BET_MORE_THAN_CASH:
			errorDesc = "Bet more than cash.";
			break;
		case BET_MORE_THAN_MAX_BET:
			errorDesc = "Bet more than max bet value.";
			break;
		case ERROR_UNKOWN:
		default:
			errorDesc = "Unknown error.";
		}
		
		return errorDesc;
	}
	
	public int getErrorCode() {
		return this.errorCode;
	}
}
