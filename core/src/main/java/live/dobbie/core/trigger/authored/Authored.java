package live.dobbie.core.trigger.authored;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;

@ContextClass
public interface Authored extends Trigger {
    @ContextVar(path = {"author_name"}, parser = Author.NameConverter.class)
    @NonNull Author getAuthor();

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .set("author", getAuthor())
                .copy(Trigger.super.toLocString(loc));
    }
}
