package live.dobbie.core.source;

import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public interface Source extends Cleanable {
    @NonNull User getUser();

    @NonNull List<Trigger> triggerList();

    @NonNull
    default Stream<Trigger> triggerStream() {
        return triggerList().stream();
    }

    interface Factory<S extends Source> {
        @NonNull S createSource(@NonNull User user);

        interface Provider {
            @NonNull List<Factory> getList();

            class Mutable implements Provider {
                private final List<Factory> factories = new ArrayList<>();

                @Override
                public @NonNull List<Factory> getList() {
                    return factories;
                }
            }

            class Immutable implements Provider {
                private final List<Factory> factories;

                public Immutable(@NonNull List<Factory> factories) {
                    this.factories = Collections.unmodifiableList(factories);
                }

                @Override
                public @NonNull List<Factory> getList() {
                    return factories;
                }
            }
        }
    }

    @RequiredArgsConstructor
    abstract class UsingQueue implements Source {
        protected final @NonNull
        @Getter
        User user;
        private final ConcurrentLinkedQueue<Trigger> queue = new ConcurrentLinkedQueue<>();

        @Override
        @NonNull
        public List<Trigger> triggerList() {
            if (queue.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList<Trigger> list = new ArrayList<>();
            while (!queue.isEmpty()) {
                list.add(queue.remove());
            }
            return list;
        }

        protected void push(@NonNull Trigger trigger) {
            queue.add(trigger);
        }
    }
}
