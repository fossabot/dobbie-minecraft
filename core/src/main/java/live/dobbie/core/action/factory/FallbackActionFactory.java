package live.dobbie.core.action.factory;

import live.dobbie.core.action.Action;
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
    class Instance implements Action.Factory<Trigger> {
        @NonNull ForTrigger forTrigger;
        @NonNull ForPlayerTrigger forPlayerTrigger;
        @NonNull ForCancellableTrigger forCancellableTrigger;
        @NonNull Loc loc;

        public Instance(@NonNull Loc loc) {
            this(new ForTrigger(loc), new ForPlayerTrigger(loc), new ForCancellableTrigger(loc), loc);
        }

        @Override
        public @NonNull Action<Trigger> createAction(@NonNull Trigger trigger) {
            ArrayList<Action<?>> list = new ArrayList<>();
            if (trigger instanceof Cancellable) {
                list.add(forCancellableTrigger.createAction((Cancellable) trigger));
            }
            if (trigger instanceof UserRelatedTrigger) {
                list.add(forPlayerTrigger.createAction((UserRelatedTrigger) trigger));
            }
            list.add(forTrigger.createAction(trigger));
            return new Action.List(trigger, loc.withKey("fallback actions"), list);
        }
    }

    @RequiredArgsConstructor
    class ForTrigger implements Action.Factory<Trigger> {
        private static final ILogger LOGGER = Logging.getLogger(ForTrigger.class);
        private final Loc loc;

        @Override
        public @NonNull Action<Trigger> createAction(@NonNull Trigger trigger) {
            return new Action.WithDescription<Trigger>(trigger, loc.withKey("fallback action for any trigger")) {
                @Override
                public void execute() {
                    LOGGER.error("Could not find action for " + trigger);
                }
            };
        }
    }

    @RequiredArgsConstructor
    class ForPlayerTrigger implements Action.Factory<UserRelatedTrigger> {
        private final Loc loc;

        @Override
        public @NonNull Action<UserRelatedTrigger> createAction(@NonNull UserRelatedTrigger trigger) {
            return new Action.WithDescription<UserRelatedTrigger>(trigger, loc.withKey("fallback action for user related triggers")) {
                @Override
                public void execute() {
                    trigger.getUser().sendErrorLocMessage(
                            loc.withKey("Dobbie could not find action for the following trigger: {trigger}")
                                    .set("trigger", trigger)
                    );
                }
            };
        }
    }

    @RequiredArgsConstructor
    class ForCancellableTrigger implements Action.Factory<Cancellable> {
        private static final ILogger LOGGER = Logging.getLogger(ForCancellableTrigger.class);
        private final Loc loc;

        @Override
        public @NonNull Action<Cancellable> createAction(@NonNull Cancellable trigger) {
            return new Action.WithDescription<Cancellable>(trigger, loc.withKey("fallback action for cancellable triggers")) {
                @Override
                public void execute() {
                    LOGGER.error("Cancelling trigger " + trigger);
                    if (!trigger.isCancelled()) {
                        trigger.cancel(new Cancellation(CancellationType.FATAL, loc.withKey("Reached fallback action factory: cancelled by default")));
                    }
                }
            };
        }
    }
}
