package live.dobbie.core.trigger.custom;

import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.user.User;
import live.dobbie.core.util.Cleanable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomTriggerSource implements Cleanable {
    private final List<Trigger> triggers = new ArrayList<>();

    public Source ofUser(@NonNull User user) {
        return new OfUser(user);
    }

    public void push(@NonNull Trigger trigger) {
        triggers.add(trigger);
    }

    public void flush() {
        triggers.clear();
    }

    @Override
    public void cleanup() {
        flush();
    }

    public class OfUser implements Source {
        private final @NonNull User user;

        public OfUser(@NonNull User user) {
            this.user = user;
        }

        @Override
        public @NonNull User getUser() {
            return user;
        }

        @Override
        public @NonNull List<Trigger> triggerList() {
            return triggers;
        }

        @Override
        public void cleanup() {
        }
    }
}
