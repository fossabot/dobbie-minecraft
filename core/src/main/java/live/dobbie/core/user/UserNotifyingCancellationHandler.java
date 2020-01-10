package live.dobbie.core.user;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.trigger.cancellable.Cancellable;
import live.dobbie.core.trigger.cancellable.Cancellation;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserNotifyingCancellationHandler implements CancellationHandler {
    private final @NonNull Loc loc;

    @Override
    public void cancel(@NonNull Cancellable cancellable, @NonNull Cancellation cancellation) {
        // TODO keep in mind there are "soft" cancellations when they are implemented
        if (!(cancellable instanceof UserRelatedTrigger)) {
            return;
        }
        User user = ((UserRelatedTrigger) cancellable).getUser();
        user.sendLocMessage(loc.withKey("Trigger was cancelled \"{trigger}\" because \"{reason}\"")
                .set("trigger", cancellable)
                .set("reason", cancellation.getReason())
        );
    }
}
