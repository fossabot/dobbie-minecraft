package live.dobbie.core.dest;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.ActionExecutionException;
import live.dobbie.core.dest.cmd.Cmd;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdExecutionException;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;

public class DestSectionAction extends Action<Trigger> {
    private final @NonNull DestSection section;
    private final @NonNull CmdContext cmdContext;
    private final @NonNull Loc loc;

    public DestSectionAction(@NonNull Trigger trigger, @NonNull DestSection section,
                             @NonNull CmdContext cmdContext, @NonNull Loc loc) {
        super(trigger);
        this.section = section;
        this.cmdContext = cmdContext;
        this.loc = loc;
    }

    @Override
    public void execute() throws ActionExecutionException {
        try {
            Cmd.executeFrom(section.getCommands(), cmdContext);
        } catch (CmdExecutionException e) {
            throw new ActionExecutionException(this, loc, e);
        }
    }

    @Override
    public @NonNull LocString toLocString(@NonNull Loc loc) {
        return loc.withKey("Action from section called \"{section_name}\" in response to trigger \"{trigger}\"")
                .set("section_name", section.getDisplayName() == null ? section.getName() : section.getDisplayName())
                .set("trigger", trigger);
    }
}
