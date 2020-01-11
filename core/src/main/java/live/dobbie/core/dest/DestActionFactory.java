package live.dobbie.core.dest;

import live.dobbie.core.action.ActionFactory;
import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.value.ContextualCondition;
import live.dobbie.core.dest.cmd.CmdContext;
import live.dobbie.core.dest.cmd.CmdContextFactory;
import live.dobbie.core.exception.ComputationException;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DestActionFactory implements ActionFactory {
    private static final ILogger LOGGER = Logging.getLogger(DestActionFactory.class);

    private final @NonNull CmdContextFactory cmdContextFactory;
    private final @NonNull UserSettingsProvider settingsProvider;
    private final @NonNull SettingsSubscription<DestMap> fallbackSettings;
    private final @NonNull Loc loc;

    @Override

    public DestSectionAction createAction(@NonNull Trigger trigger) {
        LOGGER.tracing("creating action: " + trigger);
        if (!(trigger instanceof DestAwareTrigger)) {
            LOGGER.tracing("not a " + DestAwareTrigger.class);
            return null;
        }
        DestMap destMap = getDestMap(trigger);
        if (destMap == null) {
            LOGGER.warning("no DestMap for " + trigger);
            return null;
        }
        String destName = getDestName(trigger);
        Dest dest = destMap.require(destName);
        CmdContext cmdContext = cmdContextFactory.generateContext(trigger);
        DestSection section = getDestAction(dest, trigger, cmdContext.getObjectContext());
        if (section == null) {
            LOGGER.warning("no " + DestSection.class + " found for " + trigger);
            return null;
        }
        return new DestSectionAction(trigger, section, cmdContext, loc);
    }

    @NonNull
    private String getDestName(Trigger trigger) {
        String destName = ((DestAwareTrigger) trigger).getPreferredDestination();
        if (destName == null) {
            LOGGER.tracing("preferred destination is null, using name");
            destName = trigger.getName();
        }
        return destName;
    }


    private DestMap getDestMap(Trigger trigger) {
        if (trigger instanceof UserRelatedTrigger) {
            LOGGER.tracing("is UserRelatedTrigger, querying UserSettingsProvider");
            User user = ((UserRelatedTrigger) trigger).getUser();
            return settingsProvider.get(user).getValue(DestMap.class);
        } else {
            LOGGER.tracing("not UserRelatedTrigger, querying fallback settings");
            return fallbackSettings.getValue();
        }
    }


    private DestSection getDestAction(Dest dest, Trigger trigger, ObjectContext objectContext) {
        LOGGER.tracing("selecting action for " + trigger + " using " + objectContext);
        for (DestSection section : dest.getSections().values()) {
            ContextualCondition condition = section.getCondition();
            if (condition == null) {
                LOGGER.tracing("condition of " + section.getName() + " is null");
                continue;
            }
            boolean conditionMet;
            try {
                conditionMet = condition.isTrue(objectContext);
            } catch (ComputationException e) {
                throw new RuntimeException(e);
            }
            if (conditionMet) {
                LOGGER.tracing("found eligible section: " + section.getName());
                return section;
            } else {
                LOGGER.tracing("condition not met in section: " + section.getName());
            }
        }
        LOGGER.tracing("no action is eligible for this trigger");
        return null;
    }
}
