package ua.kh.khpi.alex_babenko.exceptions;

public class EmptyDataException extends RuntimeException {

	private static final long serialVersionUID = 3882342870300224761L;

	public EmptyDataException() {
		super();
	}

	public EmptyDataException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EmptyDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyDataException(String message) {
		super(message);
	}

	public EmptyDataException(Throwable cause) {
		super(cause);
	}

}
