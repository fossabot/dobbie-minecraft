package live.dobbie.core.source.sa;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.source.Source;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

public class SettingsAwareFactory implements Source.Factory {
    private static final LoadingCache<Class, Constructor> cache = CacheBuilder.newBuilder()
            .weakValues()
            .build(new CacheLoader<Class, Constructor>() {
                @Override
                public Constructor load(@NonNull Class key) throws Exception {
                    return findConstructor(key);
                }
            });

    private final @NonNull Class<? extends Source> sourceClass;
    private final @NonNull UserSettingsProvider settingsProvider;
    private final Constructor<? extends Source> constructor;

    public SettingsAwareFactory(@NonNull Class<? extends Source> sourceClass, @NonNull UserSettingsProvider settingsProvider) {
        this.sourceClass = sourceClass;
        this.settingsProvider = settingsProvider;
        try {
            this.constructor = cache.get(sourceClass);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static Constructor<? extends Source> findConstructor(Class<? extends Source> sourceClass) {
        Constructor<? extends Source> constructor;
        try {
            constructor = sourceClass.getConstructor(User.class, ISettings.class);
        } catch (NoSuchMethodException e) {
            throw new Error("could not find constructor with " + User.class + " and " + ISettings.class + " in " + sourceClass, e);
        }
        SettingsAware annotation = constructor.getAnnotation(SettingsAware.class);
        if (annotation == null) {
            throw new Error("constructor " + constructor + " must be annotated with " + SettingsAware.class);
        }
        return constructor;
    }

    @Override
    public @NonNull Source createSource(@NonNull User user) {
        try {
            return constructor.newInstance(user, settingsProvider.get(user));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error calling constructor for " + sourceClass + " from reflection", e);
        }
    }
}
