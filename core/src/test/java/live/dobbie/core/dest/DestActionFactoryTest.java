package live.dobbie.core.dest;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.ActionExecutionException;
import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.context.factory.ObjectContextFactory;
import live.dobbie.core.context.value.ContextualCondition;
import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.exception.ComputationException;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.settings.ISettings;
import live.dobbie.core.settings.listener.SettingsSubscription;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.environment.EnvFactory;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class DestActionFactoryTest {

    @Test
    void test() throws ComputationException, ActionExecutionException, CmdExecutionException {
        Loc loc = new Loc();
        ObjectContextFactory objectContextFactory = Mockito.mock(ObjectContextFactory.class);
        ObjectContext objectContext = Mockito.mock(ObjectContext.class);
        ObjectContextBuilder objectContextBuilder = Mockito.mock(ObjectContextBuilder.class);
        when(objectContextBuilder.build()).thenReturn(objectContext);
        when(objectContextFactory.generateContextBuilder(notNull())).thenReturn(objectContextBuilder);
        Env env = Mockito.mock(Env.class);
        EnvFactory envFactory = Mockito.mock(EnvFactory.class);
        when(envFactory.generateEnv(notNull())).thenReturn(env);
        User user = Mockito.mock(User.class);
        ISettings settings = Mockito.mock(ISettings.class);
        ContextualCondition contextualCondition = Mockito.mock(ContextualCondition.class);
        when(contextualCondition.isTrue(notNull())).thenReturn(true);
        Cmd cmd = Mockito.mock(Cmd.class);
        when(cmd.execute(notNull())).thenReturn(CmdResult.SHOULD_CONTINUE);
        DestSection destSection = new DestSection("testSection", null, contextualCondition, Collections.singletonList(cmd));
        Dest dest = new Dest("test", Collections.singletonMap("testSection", destSection));
        DestMap destMap = Mockito.mock(DestMap.class);
        when(destMap.require(eq("test"))).thenReturn(dest);
        when(settings.getValue(eq(DestMap.class))).thenReturn(destMap);
        UserSettingsProvider userSettingsProvider = Mockito.mock(UserSettingsProvider.class);
        when(userSettingsProvider.get(user)).thenReturn(settings);
        SettingsSubscription<DestMap> fallbackSubscription = Mockito.mock(SettingsSubscription.class);
        DestActionFactory actionFactory = new DestActionFactory(
                new CmdContextFactory(objectContextFactory, Mockito.mock(PlainCmd.Executor.class)),
                userSettingsProvider,
                fallbackSubscription,
                loc
        );
        DestTrigger trigger = new DestTrigger(user, "test", true);
        Action action = actionFactory.createAction(trigger);
        assertNotNull(action);
        action.execute();
        verify(cmd).execute(notNull());

        assertNull(actionFactory.createAction(
                new DestTrigger(user, "doesn't exist", false)
        ));
    }

    @Value
    public static class DestTrigger implements UserRelatedTrigger, DestAwareTrigger {
        @NonNull User user;
        @NonNull String preferredDestination;
        boolean required;

        @Override
        public @NonNull LocString toLocString(@NonNull Loc loc) {
            return loc.withKey("Test dest trigger")
                    .copy(UserRelatedTrigger.super.toLocString(loc))
                    .copy(DestAwareTrigger.super.toLocString(loc));
        }

        @Override
        public @NonNull Instant getTimestamp() {
            return Instant.now();
        }

        @Override
        public @NonNull String getSource() {
            return "test_source";
        }

        @Override
        public @NonNull String getName() {
            return "invalid_name";
        }

        @Override
        public boolean isDestinationRequired() {
            return required;
        }
    }

}