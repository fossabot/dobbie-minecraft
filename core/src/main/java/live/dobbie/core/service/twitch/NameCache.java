package live.dobbie.core.service.twitch;

import com.github.twitch4j.helix.domain.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class NameCache {
    private static final ILogger LOGGER = Logging.getLogger(NameCache.class);

    private final @NonNull TwitchInstance instance;

    private final LoadingCache<String, User> userCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .softValues()
            .build(new CacheLoader<String, User>() {
                @Override
                public User load(@NonNull String userId) throws Exception {
                    return fetchUser(userId, null);
                }
            });

    private final LoadingCache<String, String> displayNameCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .softValues()
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(@NonNull String userId) throws Exception {
                    return userCache.get(userId).getDisplayName();
                }
            });

    private final LoadingCache<String, String> loginCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .softValues()
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(@NonNull String userId) throws Exception {
                    return userCache.get(userId).getLogin();
                }
            });

    public String getId(@NonNull String login) {
        String cachedId = getCachedId(login);
        if (cachedId != null) {
            return cachedId;
        }
        String id = fetchId(login);
        if (id != null) {
            loginCache.put(id, login);
        }
        return id;
    }

    public @NonNull String requireId(@NonNull String login) {
        return Objects.requireNonNull(getId(login), "could not get id by login: " + login);
    }

    public String getLogin(@NonNull String userId) {
        try {
            return loginCache.get(userId);
        } catch (ExecutionException e) {
            return null;
        }
    }

    public @NonNull String requireLogin(@NonNull String userId) {
        return Objects.requireNonNull(getLogin(userId), "could not get login of " + userId);
    }

    public String getDisplayName(@NonNull String userId) {
        try {
            return displayNameCache.get(userId);
        } catch (ExecutionException e) {
            return null;
        }
    }

    @NonNull
    public String getDisplayNameOrLogin(@NonNull String userId) {
        String name = getLogin(userId);
        String displayName = getDisplayName(userId);
        if (name == null && displayName == null) {
            throw new RuntimeException("could not get nor login, nor displayName of " + userId);
        }
        return displayName == null ? name : displayName;
    }

    @NonNull
    public String getDisplayNameOrLogin(@NonNull String userId, @NonNull String login) {
        String displayName = getDisplayName(userId);
        return displayName == null ? login : displayName;
    }

    @NonNull
    private User fetchUser(String userId, String login) {
        final String token = getToken();
        if (token == null) {
            throw new RuntimeException("token not defined");
        }
        return instance.getClient().getHelix()
                .getUsers(
                        token,
                        userId != null ? Collections.singletonList(userId) : null,
                        login != null ? Collections.singletonList(login) : null
                )
                .execute()
                .getUsers()
                .get(0);
    }

    private String getCachedId(@NonNull String login) {
        return loginCache.asMap().entrySet().stream()
                .filter(entry -> entry.getValue().equals(login))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private String fetchId(@NonNull String login) {
        User user;
        try {
            user = fetchUser(null, login);
        } catch (Exception e) {
            return null;
        }
        return user.getId();
    }

    private String getToken() {
        TwitchSettings.Global value = instance.getSubscription().getValue();
        return value == null ? null : value.getClient().getToken();
    }
}
