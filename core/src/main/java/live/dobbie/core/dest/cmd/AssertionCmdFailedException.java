package live.dobbie.core.dest.cmd;

public class AssertionCmdFailedException extends CmdExecutionException {
    public AssertionCmdFailedException(String assertion) {
        super("assertion failed: " + assertion);
    }
}
