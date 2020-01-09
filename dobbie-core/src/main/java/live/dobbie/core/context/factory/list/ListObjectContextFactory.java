package live.dobbie.core.context.factory.list;

import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.context.factory.ObjectContextFactory;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListObjectContextFactory implements ObjectContextFactory {
    private final @NonNull ObjectContextFactory delegateContext;
    private final @NonNull List<ObjectContextInitializer> initializerList;

    @Override
    public @NonNull ObjectContextBuilder generateContextBuilder(@NonNull Trigger trigger) {
        ObjectContextBuilder cb = delegateContext.generateContextBuilder(trigger);
        initializerList.forEach(initializer -> initializer.initialize(cb, trigger));
        return cb;
    }
}
