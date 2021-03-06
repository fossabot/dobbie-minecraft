package live.dobbie.core.service.twitch;

import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.context.factory.list.ObjectContextInitializer;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.trigger.TwitchTrigger;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TwitchInterface {
    private final @NonNull TwitchInstance twitchInstance;
    private final @NonNull TwitchChannel channel;

    public void sendChatMessage(String message) {
        twitchInstance.sendMessage(channel, message);
    }

    @RequiredArgsConstructor
    public static class AsObjectContextInitializer implements ObjectContextInitializer {
        @Override
        public void initialize(@NonNull ObjectContextBuilder cb, @NonNull Trigger trigger) {
            if (!(trigger instanceof TwitchTrigger)) {
                return;
            }
            TwitchTrigger twitchTrigger = (TwitchTrigger) trigger;
            cb.set("twitch", new TwitchInterface(twitchTrigger.getTwitchInstance(), twitchTrigger.getChannel()));
        }
    }
}
