package live.dobbie.core.service.twitch;

import com.github.philippheuer.events4j.EventManager;
import com.github.philippheuer.events4j.domain.Event;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ParallelFlux;
import reactor.core.publisher.TopicProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.WaitStrategy;

import java.util.Calendar;
import java.util.UUID;

public class DobbieTwitch4jEventManager extends EventManager {
    // private static final ILogger LOGGER = Logging.getLogger(DobbieTwitch4jEventManager.class);

    private final Scheduler scheduler;
    private final FluxProcessor<Event, Event> processor;
    private final FluxSink<Event> sink;

    public DobbieTwitch4jEventManager(Scheduler scheduler, FluxProcessor<Event, Event> processor, FluxSink.OverflowStrategy overflowStrategy) {
        super(scheduler, new MockFluxProcessor(), overflowStrategy);
        this.scheduler = scheduler;
        this.processor = processor;
        this.sink = this.processor.sink(overflowStrategy);
    }

    @Override
    public void dispatchEvent(Event event) {
        // LOGGER.debug("Pending: " + ((TopicProcessor<Event>) this.processor).getPending());
        // LOGGER.debug("Event: " + event);
        // super.dispatchEvent(event);
        setupEvent(event);
        this.sink.next(event);
    }

    private void setupEvent(Event event) {
        event.setFiredAt(Calendar.getInstance());
        event.setServiceMediator(getServiceMediator());
        event.setEventId(UUID.randomUUID().toString());
    }

    @Override
    public <E extends Event> ParallelFlux<E> onEvent(Class<E> eventClass) {
        return processor
                .ofType(eventClass)
                .parallel()
                .runOn(this.scheduler);
    }

    // create optimized EventManager
    public static DobbieTwitch4jEventManager create() {
        return new DobbieTwitch4jEventManager(
                Schedulers.newParallel(
                        "dobbie-twitch4j-scheduler",
                        Runtime.getRuntime().availableProcessors()
                ),
                TopicProcessor.<Event>builder()
                        .name("dobbie-twitch4j-processor")
                        // decrease CPU usage
                        .waitStrategy(WaitStrategy.sleeping())
                        // buffer was too big
                        .bufferSize(1024)
                        .build(),
                FluxSink.OverflowStrategy.BUFFER
        );
    }

    // created the same used in original EventManager
    public static DobbieTwitch4jEventManager createOld() {
        return new DobbieTwitch4jEventManager(
                Schedulers.newParallel("events4j-scheduler", Runtime.getRuntime().availableProcessors() * 2),
                TopicProcessor.create("events4j-processor", 8192),
                FluxSink.OverflowStrategy.BUFFER
        );
    }

    private static class MockFluxProcessor extends FluxProcessor {
        @Override
        public void onSubscribe(Subscription s) {
        }

        @Override
        public void onNext(Object o) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void subscribe(CoreSubscriber actual) {
        }
    }
}
