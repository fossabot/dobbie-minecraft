package live.dobbie.core.script;

public class ScriptCompilationException extends Exception {
    public ScriptCompilationException(String message) {
        super(message);
    }

    public ScriptCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptCompilationException(Throwable cause) {
        super(cause);
    }
}
