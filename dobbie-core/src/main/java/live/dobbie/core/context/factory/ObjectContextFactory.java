package live.dobbie.core.context.factory;

import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.context.SimpleContext;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

public interface ObjectContextFactory {
    @NonNull ObjectContextBuilder generateContextBuilder(@NonNull Trigger trigger);

    @RequiredArgsConstructor
    class Delegated implements ObjectContextFactory {
        private final @NonNull
        @Delegate
        ObjectContextFactory factory;
    }

    class Simple implements ObjectContextFactory {
        public static final Simple INSTANCE = new Simple();

        @Override
        public @NonNull ObjectContextBuilder generateContextBuilder(@NonNull Trigger trigger) {
            return SimpleContext.builder();
        }
    }
}
