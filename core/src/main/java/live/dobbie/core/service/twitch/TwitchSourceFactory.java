package live.dobbie.core.service.twitch;

import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class TwitchSourceFactory implements Source.Factory<TwitchSource> {
    private final @NonNull TwitchInstance twitchInstance;
    private final @NonNull CancellationHandler cancellationHandler;
    private final @NonNull UserSettingsProvider userSettingsProvider;
    private final @NonNull NameCache nameCache;

    @NonNull
    @Override
    public TwitchSource createSource(@NonNull User user) {
        return new TwitchSource(new TwitchClient(twitchInstance, nameCache), cancellationHandler, user, userSettingsProvider.get(user), nameCache);
    }
}
