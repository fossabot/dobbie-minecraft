package live.dobbie.core.exception;

public class ComputationException extends Exception {
    public ComputationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComputationException(Throwable cause) {
        super(cause);
    }
}
