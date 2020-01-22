package live.dobbie.core.trigger;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextComplexVar;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.loc.ToLocString;
import live.dobbie.core.misc.primitive.converter.DateTimeConverters;
import lombok.NonNull;

import java.time.Instant;

@ContextClass
public interface Trigger extends ToLocString {
    @ContextComplexVar({
            @ContextVar(path = {"timestamp_utc_millis"}, parser = DateTimeConverters.InstantToMillis.class),
            @ContextVar(parser = DateTimeConverters.ToDateTime.class)
    })
    @NonNull Instant getTimestamp();

    @ContextVar
    @NonNull String getSource();

    // TODO cache reflection usage?
    @ContextVar
    default @NonNull String getName() {
        NamedTrigger namedTrigger = getClass().getAnnotation(NamedTrigger.class);
        if (namedTrigger == null) {
            throw new Error(getClass() + " not annotated by " + NamedTrigger.class);
        }
        return namedTrigger.value();
    }

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .set("timestamp", getTimestamp().toString())
                .set("source", getSource())
                .set("name", getName());
    }
}
