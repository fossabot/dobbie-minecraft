package live.dobbie.core.dest.cmd;

public class CmdExecutionException extends Exception {
    public CmdExecutionException(String message) {
        super(message);
    }

    public CmdExecutionException(Throwable cause) {
        super(cause);
    }

    public CmdExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
