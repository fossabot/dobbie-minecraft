package live.dobbie.core.service.streamlabs;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.service.ServiceRefProvider;
import live.dobbie.core.service.streamlabs.api.StreamLabsApi;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StreamLabsSourceFactory implements Source.Factory<StreamLabsSource> {
    private final @NonNull ServiceRefProvider serviceRefProvider;
    private final @NonNull CancellationHandler cancellationHandler;
    private final @NonNull UserSettingsProvider userSettingsProvider;
    private final @NonNull Loc loc;

    @NonNull
    @Override
    public StreamLabsSource createSource(@NonNull User user) {
        return new StreamLabsSource(
                user,
                userSettingsProvider.get(user),
                serviceRefProvider.createReference(StreamLabsApi.class, user),
                loc,
                cancellationHandler
        );
    }
}
