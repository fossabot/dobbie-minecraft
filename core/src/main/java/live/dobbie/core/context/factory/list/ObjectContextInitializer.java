package live.dobbie.core.context.factory.list;

import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;

public interface ObjectContextInitializer {
    void initialize(@NonNull ObjectContextBuilder cb, @NonNull Trigger trigger);
}
