package live.dobbie.core.service.twitch.event;

import live.dobbie.core.service.twitch.data.TwitchChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
@Getter
@EqualsAndHashCode(callSuper = true)
public class ChannelTitleChangedEvent extends CustomTwitchChannelEvent {
    private final String title;

    public ChannelTitleChangedEvent(@NonNull TwitchChannel channel, @NonNull String title) {
        super(channel);
        this.title = title;
    }
}
