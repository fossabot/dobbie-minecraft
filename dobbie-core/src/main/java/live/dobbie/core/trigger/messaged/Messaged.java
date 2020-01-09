package live.dobbie.core.trigger.messaged;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;


@ContextClass
public interface Messaged extends Trigger {
    @ContextVar(nullable = true)
    Message getMessage();

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .set("message", getPlainMessage(this))
                .copy(Trigger.super.toLocString(loc));
    }


    static String getPlainMessage(Trigger trigger) {
        if (trigger instanceof Messaged) {
            Messaged messaged = (Messaged) trigger;
            if (messaged.getMessage() != null) {
                return messaged.getMessage().toPlainString();
            }
        }
        return null;
    }
}
