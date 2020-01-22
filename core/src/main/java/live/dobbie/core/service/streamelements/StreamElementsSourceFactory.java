package live.dobbie.core.service.streamelements;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StreamElementsSourceFactory implements Source.Factory<StreamElementsSource> {
    private final @NonNull UserSettingsProvider userSettingsProvider;
    private final @NonNull Loc loc;
    private final @NonNull CancellationHandler cancellationHandler;

    @NonNull
    @Override
    public StreamElementsSource createSource(@NonNull User user) {
        return new StreamElementsSource(user, userSettingsProvider.get(user), loc, cancellationHandler);
    }
}
