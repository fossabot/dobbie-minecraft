package live.dobbie.core.service.twitch;

import live.dobbie.core.service.twitch.data.trigger.TwitchChatTrigger;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class TwitchChatSourceFactory implements Source.Factory<TwitchChatSource> {
    private final @NonNull TwitchInstance twitchInstance;
    private final @NonNull CancellationHandler.Factory<TwitchChatTrigger> cancellationHandlerFactory;
    private final @NonNull UserSettingsProvider userSettingsProvider;
    private final NameCache nameCache;

    @NonNull
    @Override
    public TwitchChatSource createSource(@NonNull User user) {
        return new TwitchChatSource(new TwitchChatClient(twitchInstance), cancellationHandlerFactory.create(user), user, userSettingsProvider.get(user), nameCache);
    }
}
