package live.dobbie.core.action;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.loc.ToLocString;
import live.dobbie.core.trigger.Trigger;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@RequiredArgsConstructor
@ToString(of = "trigger")
public abstract class Action implements ToLocString {
    protected final @NonNull
    @Getter
    Trigger trigger;

    public abstract void execute() throws ActionExecutionException;

    public abstract static class WithDescription extends Action {
        private final @NonNull LocString description;

        public WithDescription(@NonNull Trigger trigger, @NonNull LocString description) {
            super(trigger);
            this.description = description;
        }

        @Override
        @NonNull
        public LocString toLocString(@NonNull Loc loc) {
            return loc.withKey("Action with description: {action_description}")
                    .set("action_description", description);
        }
    }

    public static class OfRunnable extends Action.WithDescription {
        private final @NonNull Runnable runnable;

        public OfRunnable(@NonNull Trigger trigger, @NonNull LocString description, @NonNull Runnable runnable) {
            super(trigger, description);
            this.runnable = runnable;
        }

        @Override
        public final void execute() throws ActionExecutionException {
            try {
                runnable.run();
            } catch (RuntimeException rE) {
                throw new ActionExecutionException(rE);
            }
        }
    }

    public static class List extends Action.WithDescription {
        private final @NonNull java.util.List<Action> actions;

        public List(@NonNull Trigger trigger, @NonNull LocString description, @NonNull java.util.List<Action> actions) {
            super(trigger, description);
            this.actions = actions;
        }

        @Override
        public void execute() throws ActionExecutionException {
            for (Action action : actions) {
                try {
                    action.execute();
                } catch (Exception e) {
                    throw new ActionExecutionException("could not execute action " + action, e);
                }
            }
        }

        @Override
        public @NonNull LocString toLocString(@NonNull Loc loc) {
            return loc.withKey("action list with description: {action_description}; inherited actions: {inherited_actions}")
                    .set("action_description", super.toLocString(loc))
                    .set("inherited_actions", collectLocStringFromInheritActions(loc));
        }

        private String collectLocStringFromInheritActions(Loc loc) {
            StringBuilder sb = new StringBuilder();
            actions.forEach(action -> sb.append(", ").append(action.toLocString(loc).build()));
            return sb.toString();
        }
    }
}
