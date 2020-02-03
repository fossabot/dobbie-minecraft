package live.dobbie.core.service.twitch.event;

import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchGame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
@Getter
@EqualsAndHashCode(callSuper = true)
public class ChannelGameChangedEvent extends CustomTwitchChannelEvent {
    private final TwitchGame game;

    public ChannelGameChangedEvent(@NonNull TwitchChannel channel, TwitchGame game) {
        super(channel);
        this.game = game;
    }
}
