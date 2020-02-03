package live.dobbie.core.service.twitch;

import com.github.philippheuer.events4j.EventManager;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netflix.hystrix.HystrixCommand;
import live.dobbie.core.service.twitch.data.TwitchChannel;
import live.dobbie.core.service.twitch.data.TwitchGame;
import live.dobbie.core.service.twitch.event.ChannelGameChangedEvent;
import live.dobbie.core.service.twitch.event.ChannelGoLiveEvent;
import live.dobbie.core.service.twitch.event.ChannelGoOfflineEvent;
import live.dobbie.core.service.twitch.event.ChannelTitleChangedEvent;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public
class ChannelOnlineObserver implements Cleanable {
    private static final ILogger LOGGER = Logging.getLogger(ChannelOnlineObserver.class);
    private static final String LIVE_TYPE = "live";

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setNameFormat(ChannelOnlineObserver.class.getSimpleName() + "-%d")
                    .setDaemon(true)
                    .build()
    );
    private ScheduledFuture workerFuture;

    private final CopyOnWriteArraySet<ObservedChannel> observedChannels = new CopyOnWriteArraySet<>();

    private final @NonNull TwitchInstance instance;
    private final @NonNull NameCache nameCache;
    private final @NonNull GameCache gameCache;
    private final long initialDelayMillis, delayMillis;

    public synchronized void startObserving(@NonNull String channelId) {
        LOGGER.debug("starting observing " + channelId);
        observedChannels.add(new ObservedChannel(channelId));
        startWorker();
    }

    public synchronized void stopObserving(@NonNull String channelId) {
        LOGGER.debug("stopping observing " + channelId);
        boolean removeResult = observedChannels.removeIf(o -> o.getChannelId().equals(channelId));
        if (!removeResult) {
            LOGGER.warning("Did not observe channel " + channelId + ", but it was requested to be removed.");
        }
        if (observedChannels.isEmpty()) {
            stopWorker();
        }
    }

    private synchronized void startWorker() {
        LOGGER.debug("starting worker");
        if (workerFuture != null) {
            LOGGER.debug("already started!");
            return;
        }
        workerFuture = service.scheduleWithFixedDelay(this::workerLoop, initialDelayMillis, delayMillis, TimeUnit.MILLISECONDS);
    }

    private synchronized void stopWorker() {
        LOGGER.debug("stopping worker");
        if (workerFuture == null) {
            LOGGER.debug("already stopped!");
            return;
        }
        workerFuture.cancel(true);
        workerFuture = null;
    }

    private void workerLoop() {
        try {
            doWorkerLoop();
        } catch (Exception e) {
            LOGGER.error("Unexpected worker exception", e);
        }
    }

    private void doWorkerLoop() {
        // make snapshot of the list
        Map<ObservedChannel, String> map = observedChannels.stream().collect(
                Collectors.toMap(o -> o, ObservedChannel::getChannelId)
        );
        // request streams
        HystrixCommand<StreamList> streams = instance.getClient().getHelix().getStreams(
                null, null, null,
                map.size(),
                null, null, null,
                new ArrayList<>(map.values()),
                null);
        StreamList streamList = streams.execute();
        // iterate through observed channels
        for (ObservedChannel observedChannel : map.keySet()) {
            boolean wasLive = observedChannel.isOnline();
            Optional<Stream> streamOpt = streamList.getStreams().stream()
                    .filter(stream -> stream.getUserId().equals(
                            observedChannel.getChannelId()
                    ))
                    .findAny();

            if (streamOpt.isPresent()) {
                // is online
                Stream stream = streamOpt.get();
                if (LIVE_TYPE.equals(stream.getType())) {
                    // is live
                    String newTitle = null;
                    if (!stream.getTitle().equals(observedChannel.getTitle())) {
                        newTitle = stream.getTitle();
                    }

                    String gameId = observedChannel.getGameId();
                    if (Objects.equals(gameId, stream.getGameId())) {
                        gameId = stream.getGameId();
                    }

                    if (wasLive) {
                        dispatchContinuingOnlineEvent(observedChannel, newTitle, gameId);
                    } else {
                        dispatchGoneLiveEvent(observedChannel, stream);
                    }
                }
            } else {
                // is offline
                if (wasLive) {
                    dispatchGoneOfflineEvent(observedChannel);
                }/* else {
                    // we were offline and still are; did we forget to unsubscribe?
                }*/
            }
        }
    }

    private void dispatchContinuingOnlineEvent(ObservedChannel observedChannel, String newTitle, String gameId) {
        TwitchChannel twitchChannel = observedChannel.getChannel(nameCache);

        if (newTitle != null) {
            observedChannel.setTitle(newTitle);
            getEventManager().dispatchEvent(new ChannelTitleChangedEvent(twitchChannel, newTitle));
        }

        if (!Objects.equals(observedChannel.getGameId(), gameId)) {
            observedChannel.setGameId(gameId);
            TwitchGame game = null;
            if (gameId != null) {
                game = requestGame(gameId);
            }
            getEventManager().dispatchEvent(new ChannelGameChangedEvent(twitchChannel, game));
        }
    }

    private TwitchGame requestGame(String gameId) {
        TwitchGame game;
        try {
            game = gameCache.getById(gameId);
        } catch (Exception e) {
            LOGGER.error("Could not get game by id " + gameId, e);
            game = new TwitchGame(gameId, "(unknown game)", null);
        }
        return game;
    }

    private void dispatchGoneOfflineEvent(ObservedChannel observedChannel) {
        observedChannel.setOnline(false);
        observedChannel.setGameId(null);
        observedChannel.setTitle(null);
        getEventManager().dispatchEvent(new ChannelGoOfflineEvent(observedChannel.getChannel(nameCache)));
    }

    private void dispatchGoneLiveEvent(ObservedChannel observedChannel, Stream stream) {
        observedChannel.setOnline(true);
        observedChannel.setTitle(stream.getTitle());
        observedChannel.setGameId(stream.getGameId());
        getEventManager().dispatchEvent(new ChannelGoLiveEvent(
                observedChannel.getChannel(nameCache),
                stream.getStartedAt().toInstant(),
                stream.getTitle(),
                stream.getGameId() == null ? null : requestGame(stream.getGameId())
        ));
    }

    @NonNull
    private EventManager getEventManager() {
        return instance.getClient().getEventManager();
    }

    @Override
    public void cleanup() {
        LOGGER.debug("cleaning up");
        service.shutdown();
    }

    @Data
    static class ObservedChannel {
        private final @NonNull String channelId;
        TwitchChannel channel;
        String title, gameId;
        boolean online;

        public TwitchChannel getChannel(NameCache cache) {
            if (channel == null) {
                channel = new TwitchChannel(channelId, cache.getLogin(channelId), cache.getDisplayName(channelId));
            }
            return channel;
        }
    }
}
