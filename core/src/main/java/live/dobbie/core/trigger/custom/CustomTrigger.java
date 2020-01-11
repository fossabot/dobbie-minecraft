package live.dobbie.core.trigger.custom;

import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;

public interface CustomTrigger extends Trigger {
    @Override
    @NonNull
    String getName();
}
