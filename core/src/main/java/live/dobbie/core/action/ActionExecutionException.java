package live.dobbie.core.action;

import live.dobbie.core.loc.Loc;
import lombok.NonNull;


public class ActionExecutionException extends Exception {
    public ActionExecutionException(String message) {
        super(message);
    }

    public ActionExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionExecutionException(Throwable cause) {
        super(cause);
    }

    public ActionExecutionException(@NonNull Action action, @NonNull Loc loc, Throwable cause) {
        this("Could not execute action \"" + action.toLocString(loc).build() + "\"", cause);
    }
}
