package live.dobbie.core.service.twitch.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class DelegateTwitchListener implements TwitchListener {
    private final @NonNull
    @Delegate
    TwitchListener delegate;
}
