package live.dobbie.core.dest.cmd;

import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CmdRunnable implements Runnable {
    private static final ILogger LOGGER = Logging.getLogger(CmdRunnable.class);

    private final @NonNull Cmd cmd;
    private final @NonNull CmdContext context;

    @Override
    public void run() {
        try {
            cmd.execute(context);
        } catch (CmdExecutionException e) {
            LOGGER.error("Could not execute command: " + cmd, e);
        }
    }
}
