package live.dobbie.core.action;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface ActionErrorHandler {
    void reportError(Action action, Throwable error);

    @RequiredArgsConstructor
    class NotifyingUser implements ActionErrorHandler {
        private final @NonNull Loc loc;

        @Override
        public void reportError(Action action, Throwable error) {
            Trigger trigger = action.getTrigger();
            if (trigger instanceof UserRelatedTrigger) {
                reportToUser(((UserRelatedTrigger) trigger).getUser(), action, error);
            }
        }

        private void reportToUser(User user, Action action, Throwable error) {
            user.sendErrorLocMessage(loc.withKey("An error occurred during executing action \"{action}\": {error_message}")
                    .set("action", action)
                    .set("error_message", error.toString())
            );
        }

    }
}
