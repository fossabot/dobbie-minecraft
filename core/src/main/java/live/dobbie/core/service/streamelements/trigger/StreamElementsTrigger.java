package live.dobbie.core.service.streamelements.trigger;

import live.dobbie.core.dest.DestAwareTrigger;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.trigger.authored.Authored;
import live.dobbie.core.trigger.cancellable.Cancellable;
import lombok.NonNull;

public interface StreamElementsTrigger extends UserRelatedTrigger, DestAwareTrigger, Authored, Cancellable {
    @Override
    @NonNull
    default String getSource() {
        return "stream_elements";
    }

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .copy(UserRelatedTrigger.super.toLocString(loc))
                .copy(DestAwareTrigger.super.toLocString(loc))
                .copy(Authored.super.toLocString(loc))
                .copy(Cancellable.super.toLocString(loc));
    }
}
