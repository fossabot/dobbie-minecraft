package live.dobbie.core.action.factory;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.ActionFactory;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.trigger.cancellable.Cancellable;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationType;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

public interface FallbackActionFactory {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Builder
    class Instance implements ActionFactory {
        @NonNull ForTrigger forTrigger;
        @NonNull ForPlayerTrigger forPlayerTrigger;
        @NonNull ForCancellableTrigger forCancellableTrigger;
        @NonNull Loc loc;

        public Instance(@NonNull Loc loc) {
            this(new ForTrigger(loc), new ForPlayerTrigger(loc), new ForCancellableTrigger(loc), loc);
        }

        @Override
        public @NonNull Action createAction(@NonNull Trigger trigger) {
            ArrayList<Action> list = new ArrayList<>();
            list.add(forCancellableTrigger.createAction(trigger));
            list.add(forPlayerTrigger.createAction(trigger));
            list.add(forTrigger.createAction(trigger));
            return new Action.List(trigger, loc.withKey("fallback actions"), list);
        }
    }

    @RequiredArgsConstructor
    class ForTrigger implements ActionFactory {
        private static final ILogger LOGGER = Logging.getLogger(ForTrigger.class);
        private final Loc loc;

        @Override
        public @NonNull Action createAction(@NonNull Trigger trigger) {
            return new Action.WithDescription(trigger, loc.withKey("fallback action for any trigger")) {
                @Override
                public void execute() {
                    LOGGER.error("Could not find action for " + trigger);
                }
            };
        }
    }

    class ForPlayerTrigger extends ActionFactory.Typed<UserRelatedTrigger> {
        private final @NonNull Loc loc;

        public ForPlayerTrigger(@NonNull Loc loc) {
            super(UserRelatedTrigger.class);
            this.loc = loc;
        }

        @Override
        public @NonNull Action create(@NonNull UserRelatedTrigger userTrigger) {
            return new Action.WithDescription(userTrigger, loc.withKey("fallback action for user related triggers")) {
                @Override
                public void execute() {
                    userTrigger.getUser().sendErrorLocMessage(
                            loc.withKey("Dobbie could not find action for the following trigger: {trigger}")
                                    .set("trigger", trigger)
                    );
                }
            };
        }
    }

    class ForCancellableTrigger extends ActionFactory.Typed<Cancellable> {
        private static final ILogger LOGGER = Logging.getLogger(ForCancellableTrigger.class);
        private final Loc loc;

        public ForCancellableTrigger(@NonNull Loc loc) {
            super(Cancellable.class);
            this.loc = loc;
        }

        @Override
        public @NonNull Action create(@NonNull Cancellable cancellable) {
            return new Action.WithDescription(cancellable, loc.withKey("fallback action for cancellable triggers")) {
                @Override
                public void execute() {
                    LOGGER.error("Cancelling trigger " + trigger);
                    if (!cancellable.isCancelled()) {
                        cancellable.cancel(new Cancellation(
                                CancellationType.FATAL,
                                loc.withKey("Reached fallback action factory: cancelled by default")
                        ));
                    }
                }
            };
        }
    }
}
