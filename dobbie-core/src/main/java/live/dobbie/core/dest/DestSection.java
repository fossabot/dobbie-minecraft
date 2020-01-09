package live.dobbie.core.dest;

import live.dobbie.core.context.value.ContextualCondition;
import live.dobbie.core.dest.cmd.Cmd;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class DestSection {
    @NonNull String name;
    String displayName;
    ContextualCondition condition;
    @NonNull List<Cmd> commands;
}
