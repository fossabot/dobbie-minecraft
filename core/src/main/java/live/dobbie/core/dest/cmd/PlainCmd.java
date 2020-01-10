package live.dobbie.core.dest.cmd;

import live.dobbie.core.misc.Text;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;


@Value
public class PlainCmd implements Cmd {
    @NonNull String command;

    @Override
    @NonNull
    public CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
        return context.getExecutor().execute(context, command);
    }

    public interface Executor {
        @NonNull
        CmdResult execute(@NonNull CmdContext context, @NonNull String command) throws CmdExecutionException;
    }

    @RequiredArgsConstructor
    public static class Parser implements CmdParser {
        @Override
        public Cmd parse(@NonNull Text text) {
            return new PlainCmd(text.getString());
        }
    }
}
