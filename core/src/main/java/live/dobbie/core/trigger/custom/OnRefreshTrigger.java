package live.dobbie.core.trigger.custom;

import live.dobbie.core.dest.DestAwareTrigger;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Ignorable;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.user.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@AllArgsConstructor
@Value
public class OnRefreshTrigger implements CustomTrigger, DestAwareTrigger, UserRelatedTrigger, Ignorable {
    @NonNull Instant timestamp;
    @NonNull User user;

    public OnRefreshTrigger(@NonNull User user) {
        this(Instant.now(), user);
    }

    @Override
    public @NonNull String getName() {
        return "on_refresh";
    }

    @Override
    public @NonNull String getSource() {
        return "dobbie";
    }

    @Override
    public String getPreferredDestination() {
        return "debug";
    }

    @Override
    public boolean isDestinationRequired() {
        return false;
    }

    @Override
    public @NonNull LocString toLocString(@NonNull Loc loc) {
        return loc.withKey("Settings refreshed");
    }
}
