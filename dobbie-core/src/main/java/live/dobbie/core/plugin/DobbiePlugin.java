package live.dobbie.core.plugin;

import live.dobbie.core.Dobbie;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.plugin.ticker.Ticker;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserRegisterListener;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DobbiePlugin implements Cleanable, UserRegisterListener {
    private static final ILogger LOGGER = Logging.getLogger(DobbiePlugin.class);

    private final @NonNull Dobbie dobbie;
    private final @NonNull Ticker ticker;
    private final @NonNull Loc loc;

    public void start() {
        ticker.start(() -> {
            try {
                dobbie.tick();
            } catch (RuntimeException rE) {
                LOGGER.error("Error during ticking", rE);
            }
        });
    }

    @Override
    public void registerUser(@NonNull User user) {
        ticker.schedule(() -> {
            try {
                dobbie.registerUser(user);
            } catch (RuntimeException rE) {
                LOGGER.error("Could not register user " + user, rE);
                user.disconnectLoc(loc.withKey("Dobbie plugin error. Please contact administration and restart the server."));
            }
        });
    }

    @Override
    public void unregisterUser(@NonNull User user) {
        ticker.schedule(() -> {
            try {
                dobbie.unregisterUser(user);
            } catch (RuntimeException rE) {
                LOGGER.error("Could not unregister user " + user, rE);
            }
        });
    }

    @Override
    public void cleanup() {
        ticker.schedule(() -> {
            try {
                dobbie.cleanup();
            } catch (RuntimeException rE) {
                LOGGER.error("Could not cleanup Dobbie", rE);
            }
            ticker.cleanup();
        });
        boolean terminated;
        try {
            terminated = ticker.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("could not properly disable Dobbie due to interruption", e);
        }
        if (!terminated) {
            throw new RuntimeException("could not property disable Dobbie: did not terminate in 10 seconds");
        }
    }

    public static DobbiePluginBuilder builder() {
        return new DobbiePluginBuilder();
    }
}
