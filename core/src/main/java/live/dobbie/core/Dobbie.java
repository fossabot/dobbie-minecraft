package live.dobbie.core;

import live.dobbie.core.action.Action;
import live.dobbie.core.action.ActionFactory;
import live.dobbie.core.action.scheduler.ActionScheduler;
import live.dobbie.core.config.DobbieConfig;
import live.dobbie.core.service.ServiceRegistry;
import live.dobbie.core.source.Source;
import live.dobbie.core.trigger.Trigger;
import live.dobbie.core.trigger.TriggerErrorHandler;
import live.dobbie.core.user.SettingsSourceNotFoundException;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserRegisterListener;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

// TODO test trigger every dobbie tick (or when settings are updated)
// TODO merge with fabric
// TODO implement bukkit, forge, sponge, nukkit
@RequiredArgsConstructor //(access = AccessLevel.PACKAGE)
public class Dobbie implements UserRegisterListener {
    private static final ILogger LOGGER = Logging.getLogger(Dobbie.class);

    private final Map<User, Instance> userTable = new HashMap<>();

    private final @NonNull DobbieSettings settings;
    private final @NonNull Source.Factory.Provider sourceFactoryProvider;
    private final @NonNull ActionFactory actionFactory;
    private final @NonNull ActionScheduler actionScheduler;
    private final @NonNull TriggerErrorHandler errorHandler;
    private final @NonNull ServiceRegistry serviceRegistry;

    public void registerUser(@NonNull User user) {
        LOGGER.debug("Registering user: " + user);
        if (userTable.containsKey(user)) {
            LOGGER.warning("User " + user + " already registered! Unregistering first...");
            unregisterUser(user);
        }
        try {
            settings.getUserSettingsProvider().registerUser(user);
        } catch (SettingsSourceNotFoundException settingsNotFound) {
            LOGGER.warning("Dobbie will not serve user without settings (" + user.getName() + "): " + settingsNotFound.toString());
            return;
        }
        serviceRegistry.registerUser(user);
        userTable.put(user, createInstance(user));
        actionScheduler.registerUser(user);
    }

    Instance createInstance(User user) {
        List<Source.Factory> factoryList = sourceFactoryProvider.getList();
        List<Source> sourceList = new ArrayList<>();
        boolean gotError = false;

        for (Source.Factory factory : factoryList) {
            Source source;
            try {
                source = factory.createSource(user);
            } catch (RuntimeException rE) {
                LOGGER.error("Could not create source from " + factory + " for " + user, rE);
                gotError = true;
                break;
            }
            sourceList.add(source);
        }

        if (gotError) {
            LOGGER.debug("Cleaning up already created sources");
            sourceList.forEach(Cleanable::cleanup);
            sourceList.clear();
        }

        return new Instance(sourceList);
    }

    public void unregisterUser(@NonNull User user) {
        LOGGER.debug("Unregistering user: " + user);
        Instance instance = userTable.remove(user);
        if (instance == null) {
            LOGGER.warning("User was not even registered");
        } else {
            actionScheduler.unregisterUser(user);
            serviceRegistry.unregisterUser(user);
            instance.cleanup();
            settings.getUserSettingsProvider().unregisterUser(user);
        }
    }

    private int tickTimer = -1;

    public void tick() {
        LOGGER.tracing("tick");
        refreshSettings();
        scheduleActions();
    }

    private void refreshSettings() {
        if (tickTimer > -1) {
            DobbieConfig.Timer.Ticks ticks = settings.getGlobalSettings().getValue(DobbieConfig.Timer.Ticks.class);
            if (ticks != null) {
                int reloadEvery = ticks.getReloadEvery();
                if (reloadEvery <= 0) {
                    return;
                }
                if (++tickTimer < reloadEvery) {
                    return;
                }
            }
        }
        LOGGER.tracing("refreshing settings");
        tickTimer = 0;
        settings.refresh();
    }

    private void scheduleActions() {
        collectTriggers().flatMap(this::createActions).forEach(actionScheduler::schedule);
    }

    private Stream<Trigger> collectTriggers() {
        return userTable.values().stream().flatMap(Instance::collectTriggers);
    }

    private Stream<Action> createActions(Trigger trigger) {
        Action action;
        try {
            action = actionFactory.createAction(trigger);
        } catch (Exception e) {
            errorHandler.reportError(trigger, e);
            throw new RuntimeException("could not create action for " + trigger, e);
        }
        return Stream.of(action);
    }

    @Override
    public void cleanup() {
        userTable.values().forEach(Instance::cleanup);
        userTable.clear();
        actionScheduler.cleanup();
        serviceRegistry.cleanup();
    }

    Instance getInstance(User user) {
        return userTable.get(user);
    }

    @RequiredArgsConstructor
    static class Instance implements Cleanable {
        private final @NonNull List<Source> sourceList;

        Stream<Trigger> collectTriggers() {
            return sourceList.stream().flatMap(Source::triggerStream);
        }

        @Override
        public void cleanup() {
            sourceList.forEach(Cleanable::cleanup);
        }
    }
}
