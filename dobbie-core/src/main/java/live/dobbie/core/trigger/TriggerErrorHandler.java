package live.dobbie.core.trigger;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface TriggerErrorHandler {
    void reportError(Trigger trigger, Throwable error);

    @RequiredArgsConstructor
    class NotifyingUser implements TriggerErrorHandler {
        private final @NonNull Loc loc;

        @Override
        public void reportError(Trigger trigger, Throwable error) {
            if (trigger instanceof UserRelatedTrigger) {
                User user = ((UserRelatedTrigger) trigger).getUser();
                user.sendLocMessage(loc.withKey("An error occurred during processing " +
                        "trigger \"{trigger}\": {error_message}")
                        .set("trigger", trigger)
                        .set("error_message", error.toString())
                );
            }
        }
    }
}
