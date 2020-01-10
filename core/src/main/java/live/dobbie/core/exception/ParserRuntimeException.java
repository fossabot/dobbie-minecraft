package live.dobbie.core.exception;

public class ParserRuntimeException extends RuntimeException {
    public ParserRuntimeException(String message) {
        super(message);
    }

    public ParserRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
