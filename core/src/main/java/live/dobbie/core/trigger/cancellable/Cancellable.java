package live.dobbie.core.trigger.cancellable;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;

@ContextClass
public interface Cancellable extends Trigger {
    void cancel(@NonNull Cancellation cancellation);

    @ContextVar
    boolean isCancelled();

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .copy(Trigger.super.toLocString(loc));
    }
}
