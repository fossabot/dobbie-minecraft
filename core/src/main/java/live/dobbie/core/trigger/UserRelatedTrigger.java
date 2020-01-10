package live.dobbie.core.trigger;

import live.dobbie.core.context.factory.ContextObject;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.user.User;
import lombok.NonNull;


public interface UserRelatedTrigger extends Trigger {
    @ContextObject
    @NonNull User getUser();

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .set("user", getUser().getName())
                .copy(Trigger.super.toLocString(loc));
    }


    static User getUser(@NonNull Trigger trigger) {
        return trigger instanceof UserRelatedTrigger ? ((UserRelatedTrigger) trigger).getUser() : null;
    }
}
