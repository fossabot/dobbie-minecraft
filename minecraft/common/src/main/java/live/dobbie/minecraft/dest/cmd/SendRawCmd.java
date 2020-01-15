package live.dobbie.minecraft.dest.cmd;

import live.dobbie.core.dest.cmd.AbstractSubstitutorCmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.SendCmd;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.user.User;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import lombok.NonNull;

public class SendRawCmd extends SendCmd {
    public SendRawCmd(@NonNull Substitutable substitutable) {
        super(substitutable, false);
    }

    @Override
    protected void sendMessageToUser(@NonNull CmdContext context, @NonNull User user, @NonNull String message) {
        if (user instanceof MinecraftOnlinePlayer) {
            ((MinecraftOnlinePlayer) user).sendRawMessage(message);
        } else {
            super.sendMessageToUser(context, user, message);
        }
    }

    public static class Parser extends AbstractSubstitutorCmd.Parser {
        public Parser(@NonNull SubstitutableParser parser) {
            super(parser);
        }

        @Override
        protected AbstractSubstitutorCmd create(@NonNull Substitutable substitutable) {
            return new SendRawCmd(substitutable);
        }
    }
}
