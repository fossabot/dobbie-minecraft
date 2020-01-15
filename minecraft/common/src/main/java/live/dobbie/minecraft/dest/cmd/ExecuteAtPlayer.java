package live.dobbie.minecraft.dest.cmd;

import live.dobbie.core.dest.cmd.AbstractSubstitutorCmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.dest.cmd.CmdResult;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.user.User;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import live.dobbie.minecraft.compat.MinecraftServer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

public class ExecuteAtPlayer extends AbstractSubstitutorCmd {
    private final @NonNull ExecuteAtStrategy executeAtStrategy;

    public ExecuteAtPlayer(@NonNull Substitutable substitutable, @NonNull ExecuteAtStrategy executeAtStrategy) {
        super(substitutable);
        this.executeAtStrategy = executeAtStrategy;
    }

    @Override
    protected CmdResult execute(@NonNull CmdContext context, @NonNull String command) throws CmdExecutionException {
        User user = context.getUser();
        if (user == null) {
            throw new CmdExecutionException("context does not provider user, cannot execute as null");
        }
        if (!(user instanceof MinecraftOnlinePlayer)) {
            throw new CmdExecutionException("user provided in this context is not " + MinecraftOnlinePlayer.class.getSimpleName());
        }
        return executeAtStrategy.execute(context, (MinecraftOnlinePlayer) user, command);
    }

    public interface ExecuteAtStrategy {
        CmdResult execute(@NonNull CmdContext context, @NonNull MinecraftOnlinePlayer player, @NonNull String command)
                throws CmdExecutionException;
    }

    @RequiredArgsConstructor
    public static class ConsoleExecuteAt implements ExecuteAtStrategy {
        private final @NonNull Supplier<MinecraftServer> serverSupplier;

        @Override
        public CmdResult execute(@NonNull CmdContext context, @NonNull MinecraftOnlinePlayer player, @NonNull String command) throws CmdExecutionException {
            MinecraftServer minecraftServer = serverSupplier.get();
            if (minecraftServer == null) {
                throw new CmdExecutionException("minecraftServer not available");
            }
            minecraftServer.executeCommand("execute at " + player.getName() + " run " + command);
            return CmdResult.SHOULD_CONTINUE;
        }
    }

    public static class Parser extends AbstractSubstitutorCmd.Parser {
        private final @NonNull ExecuteAtStrategy executeAtStrategy;

        public Parser(@NonNull SubstitutableParser parser, @NonNull ExecuteAtStrategy executeAtStrategy) {
            super(parser);
            this.executeAtStrategy = executeAtStrategy;
        }

        @Override
        protected AbstractSubstitutorCmd create(@NonNull Substitutable substitutable) {
            return new ExecuteAtPlayer(substitutable, executeAtStrategy);
        }
    }
}
