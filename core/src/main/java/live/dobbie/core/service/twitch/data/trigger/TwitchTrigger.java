package live.dobbie.core.service.twitch.data.trigger;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextComplexVar;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.dest.DestAwareTrigger;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.service.twitch.TwitchClient;
import live.dobbie.core.service.twitch.TwitchInstance;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchUser;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.trigger.authored.Author;
import live.dobbie.core.trigger.authored.Authored;
import live.dobbie.core.trigger.cancellable.Cancellable;
import lombok.NonNull;

@ContextClass
public interface TwitchTrigger extends UserRelatedTrigger, DestAwareTrigger, Authored, Cancellable {
    @NonNull TwitchClient getClient();

    @ContextComplexVar({
            @ContextVar(path = {"twitch_channel", "id"}, parser = TwitchUser.IdConverter.class),
            @ContextVar(path = {"twitch_channel", "login"}, parser = TwitchUser.LoginConverter.class),
            @ContextVar(path = {"twitch_channel", "name"}, parser = TwitchUser.DisplayNameConverter.class)
    })
    @NonNull TwitchChannel getChannel();

    @ContextComplexVar({
            @ContextVar(path = {"twitch_author", "id"}, parser = TwitchUser.IdConverter.class),
            @ContextVar(path = {"twitch_author", "login"}, parser = TwitchUser.LoginConverter.class),
            @ContextVar(path = {"twitch_author", "name"}, parser = TwitchUser.DisplayNameConverter.class)
    })
    @NonNull TwitchUser getTwitchAuthor();

    @Override
    @NonNull
    default Author getAuthor() {
        return getTwitchAuthor();
    }

    @Override
    @NonNull
    default String getSource() {
        return "twitch";
    }

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .set("twitch_channel_login", getChannel().getLogin())
                .set("twitch_channel", getChannel().getDisplayName())
                .copy(UserRelatedTrigger.super.toLocString(loc))
                .copy(DestAwareTrigger.super.toLocString(loc))
                .copy(Authored.super.toLocString(loc))
                .copy(Cancellable.super.toLocString(loc));
    }

    @NonNull
    default TwitchInstance getTwitchInstance() {
        return getClient().getInstance();
    }
}
