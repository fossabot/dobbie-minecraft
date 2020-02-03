package live.dobbie.core.service.twitch.event;

import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchGame;
import lombok.*;

import java.time.Instant;

@Value
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChannelGoLiveEvent extends CustomTwitchChannelEvent {
    private final @NonNull Instant startTime;
    private final @NonNull String title;
    private final TwitchGame game;

    public ChannelGoLiveEvent(@NonNull TwitchChannel channel, @NonNull Instant startTime, @NonNull String title, TwitchGame game) {
        super(channel);
        this.title = title;
        this.game = game;
        this.startTime = startTime;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ChannelGoLiveEvent;
    }
}
