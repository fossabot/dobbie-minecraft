package live.dobbie.core.action.scheduler;

import live.dobbie.core.action.Action;
import live.dobbie.core.user.UserRegisterListener;
import lombok.NonNull;

public interface ActionScheduler extends UserRegisterListener {
    void schedule(@NonNull Action action);
}
