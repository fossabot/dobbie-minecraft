package live.dobbie.core.service.scheduler;

import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceRefProvider;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.user.User;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

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
        schedule(extractScheduler(serviceRefProvider, context), new CmdRunnable(enclosedCmd, context), context);
        return CmdResult.SHOULD_CONTINUE;
    }

    @NonNull
    private static IdTaskScheduler extractScheduler(@NonNull ServiceRefProvider serviceRefProvider,
                                                    @NonNull CmdContext context) throws CmdExecutionException {
        User user = context.getUser();
        if (user == null) {
            throw new CmdExecutionException("context did not contain any user");
        }
        ServiceRef<IdTaskScheduler> serviceRef = serviceRefProvider.createReference(IdTaskScheduler.class, user);
        return serviceRef.getService();
    }

    protected abstract void schedule(@NonNull IdTaskScheduler scheduler, @NonNull CmdRunnable runnable, @NonNull CmdContext context)
            throws CmdExecutionException;

    @EqualsAndHashCode(of = "waitTime", callSuper = true)
    public static class ExecuteAfter extends IdTaskScheduledCmd {
        final long waitTime;

        public ExecuteAfter(@NonNull ServiceRefProvider serviceRefProvider, @NonNull Cmd enclosedCmd, long waitTime) {
            super(serviceRefProvider, enclosedCmd);
            this.waitTime = waitTime;
        }

        @Override
        protected void schedule(@NonNull IdTaskScheduler scheduler, @NonNull CmdRunnable runnable, @NonNull CmdContext context) {
            scheduler.scheduleAfter(runnable, waitTime);
        }

        public static class Parser extends AbstractParser {
            public Parser(@NonNull ServiceRefProvider serviceRefProvider,
                          @NonNull CmdParser enclosedCmdParser) {
                super(serviceRefProvider, null, enclosedCmdParser);
            }

            @Override
            protected ExecuteAfter parse(@NonNull Text text, @NonNull Scanner scanner) throws ParserException {
                long waitTime = parseLong(scanner, "wait time");
                return new ExecuteAfter(serviceRefProvider, parseEnclosedCmd(scanner), waitTime);
            }
        }
    }

    @EqualsAndHashCode(callSuper = true)
    public static class RunLater extends IdTaskScheduledCmd {

        public RunLater(@NonNull ServiceRefProvider serviceRefProvider, @NonNull Cmd enclosedCmd) {
            super(serviceRefProvider, enclosedCmd);
        }

        @Override
        protected void schedule(@NonNull IdTaskScheduler scheduler, @NonNull CmdRunnable runnable, @NonNull CmdContext context) {
            scheduler.schedule(runnable);
        }

        public static class Parser extends AbstractParser {
            public Parser(@NonNull ServiceRefProvider serviceRefProvider,
                          @NonNull CmdParser enclosedCmdParser) {
                super(serviceRefProvider, null, enclosedCmdParser);
            }

            @Override
            protected RunLater parse(@NonNull Text text, @NonNull Scanner scanner) throws ParserException {
                return new RunLater(serviceRefProvider, parseEnclosedCmd(scanner));
            }
        }
    }

    @EqualsAndHashCode(of = {"substitutableId", "initialWait", "waitBetween"}, callSuper = true)
    public static class RepeatEvery extends IdTaskScheduledCmd {
        final Substitutable substitutableId;
        final long initialWait;
        final long waitBetween;

        public RepeatEvery(@NonNull ServiceRefProvider serviceRefProvider, @NonNull Cmd enclosedCmd,
                           @NonNull Substitutable name, long initialWait, long waitBetween) {
            super(serviceRefProvider, enclosedCmd);
            this.substitutableId = name;
            this.initialWait = initialWait;
            this.waitBetween = waitBetween;
        }

        @Override
        protected void schedule(@NonNull IdTaskScheduler scheduler, @NonNull CmdRunnable runnable, @NonNull CmdContext context) {
            IdTask id = IdTask.name(substitutableId.substitute(context.getEnvironment()));
            scheduler.scheduleRepeating(id, t -> runnable.run(), initialWait, waitBetween);
        }

        public static class Parser extends AbstractParser {
            public Parser(@NonNull ServiceRefProvider serviceRefProvider,
                          @NonNull SubstitutableParser nameParser,
                          @NonNull CmdParser enclosedCmdParser) {
                super(serviceRefProvider, nameParser, enclosedCmdParser);
            }

            @Override
            protected RepeatEvery parse(@NonNull Text text, @NonNull Scanner scanner) throws ParserException {
                Substitutable name = parseName(scanner);
                long initialWaitTime = parseLong(scanner, "initial wait time");
                long waitTime = parseLong(scanner, "wait time");
                return new RepeatEvery(serviceRefProvider, parseEnclosedCmd(scanner), name, initialWaitTime, waitTime);
            }
        }
    }

    @EqualsAndHashCode(of = "substitutableId")
    public static class CancelTask implements Cmd {
        final @NonNull ServiceRefProvider serviceRefProvider;
        final @NonNull Substitutable substitutableId;

        public CancelTask(@NonNull ServiceRefProvider serviceRefProvider, @NonNull Substitutable substitutableId) {
            this.serviceRefProvider = serviceRefProvider;
            this.substitutableId = substitutableId;
        }

        @Override
        public @NonNull CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException {
            IdTask id = IdTask.name(substitutableId.substitute(context.getEnvironment()));
            extractScheduler(serviceRefProvider, context).cancel(id);
            return CmdResult.SHOULD_CONTINUE;
        }

        @RequiredArgsConstructor
        public static class Parser implements CmdParser {
            private final @NonNull ServiceRefProvider serviceRefProvider;
            private final @NonNull SubstitutableParser nameParser;

            @Override
            public Cmd parse(@NonNull Text text) throws ParserException {
                return new CancelTask(serviceRefProvider, nameParser.parse(text.getString()));
            }
        }
    }

    @RequiredArgsConstructor
    private abstract static class AbstractParser implements CmdParser {
        private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s]+");
        protected final @NonNull ServiceRefProvider serviceRefProvider;
        private final SubstitutableParser nameParser;
        private final @NonNull CmdParser enclosedCmdParser;

        @Override
        public IdTaskScheduledCmd parse(@NonNull Text text) throws ParserException {
            Scanner scanner = new Scanner(text.getString());
            skipWhitespace(scanner);
            IdTaskScheduledCmd cmd = parse(text, scanner);
            scanner.close();
            return cmd;
        }

        protected Substitutable parseName(Scanner scanner) throws ParserException {
            Validate.notNull(nameParser, "nameParser");
            String arg;
            try {
                arg = scanner.next();
                skipWhitespace(scanner);
            } catch (RuntimeException rE) {
                throw new ParserException("could not find task name", rE);
            }
            return nameParser.parse(arg);
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
