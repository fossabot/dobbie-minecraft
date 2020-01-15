package live.dobbie.minecraft.dest.cmd;

import live.dobbie.core.dest.cmd.AbstractSubstitutorCmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.dest.cmd.CmdResult;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.user.User;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import lombok.NonNull;

public class ExecutePlayer extends AbstractSubstitutorCmd {
    public ExecutePlayer(@NonNull Substitutable substitutable) {
        super(substitutable);
    }

    @Override
    protected CmdResult execute(@NonNull CmdContext context, @NonNull String command) throws CmdExecutionException {
        User user = context.getUser();
        if (user == null) {
            throw new CmdExecutionException("context does not provide a user");
        }
        if (!(user instanceof MinecraftOnlinePlayer)) {
            throw new CmdExecutionException("user provided in this context is not " + MinecraftOnlinePlayer.class.getSimpleName());
        }
        ((MinecraftOnlinePlayer) user).executeCommand(command);
        return CmdResult.SHOULD_CONTINUE;
    }

    public static class Parser extends AbstractSubstitutorCmd.Parser {
        public Parser(@NonNull SubstitutableParser parser) {
            super(parser);
        }

        @Override
        protected AbstractSubstitutorCmd create(@NonNull Substitutable substitutable) {
            return new ExecutePlayer(substitutable);
        }
    }
}
