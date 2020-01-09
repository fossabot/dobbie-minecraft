package live.dobbie.core.dest;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;


@ContextClass
public interface DestAwareTrigger extends Trigger {
    @ContextVar(nullable = true)
    String getPreferredDestination();

    @Override
    default @NonNull LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .copy(Trigger.super.toLocString(loc));
    }
}
