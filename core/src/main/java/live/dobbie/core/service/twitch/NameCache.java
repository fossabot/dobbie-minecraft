package live.dobbie.core.service.twitch;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class NameCache {
    private static final ILogger LOGGER = Logging.getLogger(NameCache.class);

    private final @NonNull TwitchInstance instance;

    private final LoadingCache<String, String> usernames = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
        @Override
        public String load(@NonNull String key) {
            return fetchDisplayName(key);
        }
    });

    public String getDisplayName(@NonNull String userId) {
        try {
            return usernames.get(userId);
        } catch (ExecutionException e) {
            LOGGER.warning("Could not get username of " + userId, e);
            return null;
        }
    }

    @NonNull
    public String getDisplayNameOr(@NonNull String userId, @NonNull String username) {
        String displayName = getDisplayName(userId);
        return displayName == null ? username : displayName;
    }

    private String fetchDisplayName(@NonNull String userId) {
        final String token = getToken();
        if (token == null) {
            return null;
        }
        TwitchHelix helix;
        try {
            helix = instance.getClient().getHelix();
        } catch (RuntimeException rE) {
            return null;
        }
        User user = helix.getUsers(token, Collections.singletonList(userId), null).execute().getUsers().stream().findAny().orElse(null);
        if (user == null) {
            return null;
        }
        return user.getDisplayName();
    }


    private String getToken() {
        TwitchSettings.Global value = instance.getSubscription().getValue();
        if (value == null) {
            return null;
        }
        return value.getClient().getToken();
    }
}
