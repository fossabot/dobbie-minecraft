package live.dobbie.core.service.twitch;

import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.TextLocation;
import live.dobbie.core.service.twitch.data.TwitchUser;
import live.dobbie.core.service.twitch.data.trigger.TwitchTrigger;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.regex.Pattern;

@Value
public class ReplyChatCmd implements Cmd {
    @NonNull Substitutable command;

    @Override
    public @NonNull CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
        Trigger trigger = Cmd.notNull(context.getTrigger(), "trigger");
        if (!(trigger instanceof TwitchTrigger)) {
            throw new CmdExecutionException("command can only be executed on " + TwitchTrigger.class);
        }
        TwitchTrigger twitchTrigger = (TwitchTrigger) trigger;
        TwitchInstance twitchInstance = twitchTrigger.getTwitchInstance();

        String replyText = replyName(twitchTrigger.getTwitchAuthor()) + command.substitute(context.getEnvironment());
        twitchInstance.sendMessage(twitchTrigger.getChannel(), replyText);

        return CmdResult.SHOULD_CONTINUE;
    }

    private static String replyName(TwitchUser user) {
        return "@" + user.getDisplayName() + ", ";
    }

    public static class Parser extends AbstractPatternCmdParser.NameAware {
        private final @NonNull SubstitutableParser parser;

        public Parser(@NonNull String shebang, @NonNull Pattern pattern, @NonNull List<String> acceptableNameList, @NonNull SubstitutableParser parser) {
            super(shebang, pattern, acceptableNameList);
            this.parser = parser;
        }

        public Parser(@NonNull List<String> acceptableNameList, @NonNull SubstitutableParser parser) {
            super(acceptableNameList);
            this.parser = parser;
        }

        @Override
        protected Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
            return new ReplyChatCmd(parser.parse(notNull(args)));
        }
    }
}
