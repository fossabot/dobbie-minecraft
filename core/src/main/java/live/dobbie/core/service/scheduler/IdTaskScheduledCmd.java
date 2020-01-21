package live.dobbie.core.service.scheduler;

import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceRefProvider;
import live.dobbie.core.user.User;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "enclosedCmd")
public abstract class IdTaskScheduledCmd implements Cmd {
    private final @NonNull ServiceRefProvider serviceRefProvider;
    private final @NonNull Cmd enclosedCmd;

    @Override
    public @NonNull CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
        User user = context.getUser();
        if (user == null) {
            throw new CmdExecutionException("context did not contain any user");
        }
        ServiceRef<IdTaskScheduler> serviceRef = serviceRefProvider.createReference(IdTaskScheduler.class, user);
        IdTaskScheduler scheduler = serviceRef.getService();
        schedule(scheduler, new CmdRunnable(enclosedCmd, context));
        return CmdResult.SHOULD_CONTINUE;
    }

    protected abstract void schedule(@NonNull IdTaskScheduler scheduler, @NonNull CmdRunnable runnable)
            throws CmdExecutionException;

    @EqualsAndHashCode(of = "waitTime", callSuper = true)
    public static class ExecuteAfter extends IdTaskScheduledCmd {
        final long waitTime;

        public ExecuteAfter(@NonNull ServiceRefProvider serviceRefProvider, @NonNull Cmd enclosedCmd, long waitTime) {
            super(serviceRefProvider, enclosedCmd);
            this.waitTime = waitTime;
        }

        @Override
        protected void schedule(@NonNull IdTaskScheduler scheduler, @NonNull CmdRunnable runnable) {
            scheduler.scheduleAfter(runnable, waitTime);
        }

        public static class Parser extends AbstractParser {
            public Parser(@NonNull ServiceRefProvider serviceRefProvider, @NonNull CmdParser enclosedCmdParser) {
                super(serviceRefProvider, enclosedCmdParser);
            }

            @Override
            protected ExecuteAfter parse(@NonNull Text text, @NonNull Scanner scanner) throws ParserException {
                long waitTime = parseLong(scanner, "wait time");
                return new ExecuteAfter(serviceRefProvider, parseEnclosedCmd(scanner), waitTime);
            }
        }
    }

    @EqualsAndHashCode(of = {"id", "initialWait", "waitBetween"}, callSuper = true)
    public static class RepeatEvery extends IdTaskScheduledCmd {
        final @NonNull IdTask id;
        final long initialWait;
        final long waitBetween;

        public RepeatEvery(@NonNull ServiceRefProvider serviceRefProvider, @NonNull Cmd enclosedCmd,
                           @NonNull String name, long initialWait, long waitBetween) {
            super(serviceRefProvider, enclosedCmd);
            this.id = IdTask.name(name);
            this.initialWait = initialWait;
            this.waitBetween = waitBetween;
        }

        @Override
        protected void schedule(@NonNull IdTaskScheduler scheduler, @NonNull CmdRunnable runnable) {
            scheduler.scheduleRepeating(id, t -> runnable.run(), initialWait, waitBetween);
        }

        public static class Parser extends AbstractParser {
            public Parser(@NonNull ServiceRefProvider serviceRefProvider, @NonNull CmdParser enclosedCmdParser) {
                super(serviceRefProvider, enclosedCmdParser);
            }

            @Override
            protected RepeatEvery parse(@NonNull Text text, @NonNull Scanner scanner) throws ParserException {
                String name = parseString(scanner, "scheduled task name");
                long initialWaitTime = parseLong(scanner, "initial wait time");
                long waitTime = parseLong(scanner, "wait time");
                return new RepeatEvery(serviceRefProvider, parseEnclosedCmd(scanner), name, initialWaitTime, waitTime);
            }
        }
    }

    @RequiredArgsConstructor
    private abstract static class AbstractParser implements CmdParser {
        private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s]+");
        protected final @NonNull ServiceRefProvider serviceRefProvider;
        private final @NonNull CmdParser enclosedCmdParser;

        @Override
        public IdTaskScheduledCmd parse(@NonNull Text text) throws ParserException {
            Scanner scanner = new Scanner(text.getString());
            skipWhitespace(scanner);
            IdTaskScheduledCmd cmd = parse(text, scanner);
            scanner.close();
            return cmd;
        }

        protected static String parseString(Scanner scanner, String name) throws ParserException {
            String arg;
            try {
                arg = scanner.next();
                skipWhitespace(scanner);
            } catch (RuntimeException rE) {
                throw new ParserException("could not find " + name, rE);
            }
            return arg;
        }

        protected static long parseLong(Scanner scanner, String name) throws ParserException {
            long arg;
            try {
                arg = scanner.nextLong();
                skipWhitespace(scanner);
            } catch (RuntimeException rE) {
                throw new ParserException("could not find " + name, rE);
            }
            return arg;
        }

        protected static void skipWhitespace(Scanner scanner) {
            try {
                scanner.skip(WHITESPACE_PATTERN);
            } catch (NoSuchElementException ignored) {
            }
        }

        protected Cmd parseEnclosedCmd(Scanner scanner) throws ParserException {
            return enclosedCmdParser.parse(scanner.nextLine());
        }

        protected abstract IdTaskScheduledCmd parse(@NonNull Text text, @NonNull Scanner scanner) throws ParserException;
    }
}
