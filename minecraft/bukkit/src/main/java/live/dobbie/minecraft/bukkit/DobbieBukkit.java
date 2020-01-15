package live.dobbie.minecraft.bukkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import live.dobbie.core.Dobbie;
import live.dobbie.core.DobbieSettings;
import live.dobbie.core.action.factory.FallbackActionFactory;
import live.dobbie.core.action.factory.SequentalActionFactory;
import live.dobbie.core.action.scheduler.PerUserActionScheduler;
import live.dobbie.core.action.scheduler.SimpleActionScheduler;
import live.dobbie.core.context.factory.AnnotationBasedObjectContextFactory;
import live.dobbie.core.context.factory.ObjectContextFactory;
import live.dobbie.core.context.factory.list.ListObjectContextFactory;
import live.dobbie.core.context.factory.nametranslator.SnakeCaseTranslator;
import live.dobbie.core.context.factory.nametranslator.TrailingRemovingTranslator;
import live.dobbie.core.context.factory.nametranslator.VarNameTranslator;
import live.dobbie.core.context.primitive.converter.AnnotationBasedConverterProvider;
import live.dobbie.core.context.primitive.converter.PrimitiveConverterCache;
import live.dobbie.core.context.primitive.converter.PrimitiveConverterProvider;
import live.dobbie.core.context.primitive.converter.SequentalConverterProvider;
import live.dobbie.core.context.primitive.storage.PrimitiveMap;
import live.dobbie.core.context.primitive.storage.StorageAwareObjectContext;
import live.dobbie.core.context.value.ContextualCondition;
import live.dobbie.core.context.value.ScriptContextualValue;
import live.dobbie.core.dest.DestActionFactory;
import live.dobbie.core.dest.DestMap;
import live.dobbie.core.dest.DestSectionLocator;
import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.dest.cmd.script.AssertionScriptCmdParser;
import live.dobbie.core.dest.cmd.script.ConditionalScriptCmdParser;
import live.dobbie.core.dest.cmd.script.ScriptCmdParser;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.path.Path;
import live.dobbie.core.persistence.PersistenceService;
import live.dobbie.core.persistence.SessionObjectStorage;
import live.dobbie.core.plugin.DobbiePlugin;
import live.dobbie.core.plugin.ticker.ScheduledThreadPoolTicker;
import live.dobbie.core.script.js.JSModuleScriptProvider;
import live.dobbie.core.script.js.JSScriptCompiler;
import live.dobbie.core.script.js.JSScriptContext;
import live.dobbie.core.script.js.JSScriptExecutor;
import live.dobbie.core.script.js.converter.*;
import live.dobbie.core.script.js.moduleprovider.DirectoryModuleProvider;
import live.dobbie.core.script.js.moduleprovider.URIAwareModuleProvider;
import live.dobbie.core.service.ServiceRegistry;
import live.dobbie.core.service.chargeback.ChargebackService;
import live.dobbie.core.service.chargeback.ChargebackStorage;
import live.dobbie.core.service.twitch.*;
import live.dobbie.core.settings.Settings;
import live.dobbie.core.settings.source.jackson.JacksonNode;
import live.dobbie.core.settings.source.jackson.JacksonParser;
import live.dobbie.core.settings.source.jackson.JacksonSource;
import live.dobbie.core.settings.source.jackson.JacksonSourceProvider;
import live.dobbie.core.settings.source.supplier.DirectoryBasedFileSourceFactory;
import live.dobbie.core.settings.upgrader.SchemaUpgrader;
import live.dobbie.core.settings.upgrader.v.V0Upgrader;
import live.dobbie.core.source.Source;
import live.dobbie.core.substitutor.plain.PlainSubstitutorParser;
import live.dobbie.core.substitutor.plain.VarConverter;
import live.dobbie.core.trigger.TriggerErrorHandler;
import live.dobbie.core.trigger.UserRelatedTrigger;
import live.dobbie.core.trigger.cancellable.ListCancellationHandler;
import live.dobbie.core.user.SimpleUserSettingsProvider;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserNotifyingCancellationHandler;
import live.dobbie.core.util.io.FileSupplier;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.minecraft.bukkit.compat.BukkitCompat;
import live.dobbie.minecraft.bukkit.compat.BukkitPlayer;
import live.dobbie.minecraft.dest.cmd.ExecuteAtPlayer;
import live.dobbie.minecraft.dest.cmd.ExecutePlayer;
import live.dobbie.minecraft.dest.cmd.SendRawCmd;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mozilla.javascript.ContextFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class DobbieBukkit extends JavaPlugin implements Listener {
    private static final String BRAND = "bukkit";

    private @NonNull ILogger logger;

    @Override
    public void onLoad() {
        Logging.setLoggerFactory(new BukkitLogger.Factory(getLogger()));
        logger = Logging.getLogger(DobbieBukkit.class);
        bukkitCompat = new BukkitCompat(this::getServer, new BukkitScheduler(this));
    }

    private BukkitCompat bukkitCompat;
    private DobbiePlugin dobbiePlugin;

    @Override
    public void onEnable() {
        initDobbiePlugin();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void initDobbiePlugin() {
        ScheduledThreadPoolTicker ticker = new ScheduledThreadPoolTicker();
        File configFile = new File(getDataFolder(), "config.yaml");
        ObjectMapper o = new ObjectMapper(new YAMLFactory());
        SimpleModule m = new SimpleModule();
        ContextFactory cf = new ContextFactory();
        JSScriptCompiler jsScriptCompiler = new JSScriptCompiler(cf, true);
        JSScriptContext.Factory jsScriptContextFactory = new JSScriptContext.Factory(cf,
                TypedValueConverter.builder()
                        .registerConverter(new PrimitiveJSConverter(DefaultValueConverter.INSTANCE))
                        .registerConverter(new PrimitiveStorageJSConverter(DefaultValueConverter.INSTANCE))
                        .registerFromConverter(Object.class, new AccessorJSConverter(DefaultValueConverter.INSTANCE))
                        .setFallbackConverter(DefaultValueConverter.INSTANCE)
                        .build(),
                Path.of("vars"),
                new JSModuleScriptProvider(
                        new URIAwareModuleProvider(
                                new DirectoryModuleProvider(new File(getDataFolder(), "js_modules"))
                        ),
                        jsScriptCompiler
                )
        );
        JSScriptExecutor jsScriptExecutor = new JSScriptExecutor(jsScriptCompiler.getPolyfills());
        m.addDeserializer(ContextualCondition.class, new ContextualCondition.Parser(
                new ScriptContextualValue.Factory<>(
                        jsScriptContextFactory,
                        jsScriptExecutor,
                        jsScriptCompiler
                ),
                configFile.getAbsolutePath()
        ));
        SequentalCmdParser sequentalCmdParser = new SequentalCmdParser(
                new AssertionCmd.Parser(Collections.singletonList("verify")),
                new AssertionScriptCmdParser<>(
                        Arrays.asList("javascriptassert", "jsassert"),
                        jsScriptExecutor,
                        jsScriptContextFactory,
                        jsScriptCompiler
                )
        );
        m.addDeserializer(Cmd.class, new Cmd.JacksonParser(sequentalCmdParser));
        o.registerModule(m);
        JacksonParser.Provider parserProvider = new JacksonParser.Provider();
        Settings config = new Settings(
                new JacksonSource(
                        o,
                        new FileSupplier(configFile),
                        SchemaUpgrader.builder()
                                .register(new V0Upgrader(BRAND, "To receive help please visit https://dobbie.live/"))
                                .build()
                ),
                parserProvider
        );
        config.refreshValues(); // it is recommended to run it before the first tick
        TwitchInstance twitchInstance = new TwitchInstance(config);
        SimpleUserSettingsProvider<JacksonNode> userSettingsProvider = new SimpleUserSettingsProvider<>(
                new JacksonSourceProvider(
                        o,
                        null,
                        new DirectoryBasedFileSourceFactory(new File(getDataFolder(), "players"), ".yaml")
                ),
                parserProvider
        );
        PlainSubstitutorParser plainSubstitutorParser = PlainSubstitutorParser.builder()
                .defaultVarMod(VarConverter.JsonEscaping.INSTANCE)
                .varMod("de", VarConverter.DoubleJsonEscaping.INSTANCE)
                .varMod("raw", VarConverter.Identity.INSTANCE)
                .build();
        sequentalCmdParser.registerParser(
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("send"), new SendCmd.Parser(plainSubstitutorParser, false)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("sendraw"), new SendRawCmd.Parser(plainSubstitutorParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("error"), new SendCmd.Parser(plainSubstitutorParser, true)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("wait"), new WaitCmd.Parser(plainSubstitutorParser, WaitCmd.WaitStrategy.DEFAULT)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("player"), new ExecutePlayer.Parser(plainSubstitutorParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("atplayer"), new ExecuteAtPlayer.Parser(plainSubstitutorParser, new ExecuteAtPlayer.ConsoleExecuteAt(() -> bukkitCompat.getServer()))),
                new ReplyChatCmd.Parser(Arrays.asList("twitch_reply", "reply"), plainSubstitutorParser),
                new ConditionalScriptCmdParser<>(
                        Arrays.asList("javascriptif", "jsif"),
                        jsScriptExecutor,
                        jsScriptContextFactory,
                        jsScriptCompiler,
                        sequentalCmdParser
                ),
                new ScriptCmdParser<>(
                        Arrays.asList("javascript", "js"),
                        jsScriptExecutor,
                        jsScriptContextFactory,
                        jsScriptCompiler
                ),
                new GotoDestSectionCmd.Parser(
                        Arrays.asList("goto"),
                        new DestSectionLocator.Factory.UsingSettingsProvider(userSettingsProvider),
                        new DestSectionLocator.UsingSettings(config)
                ),
                new SubstitutorCmd.Parser(plainSubstitutorParser)//new ElemParser(Collections.singletonList(new AnyVarElem.Factory())))
        );
        ServiceRegistry serviceRegistry = ServiceRegistry.builder()
                .registerFactory(ChargebackService.class, new ChargebackService.RefFactory(userSettingsProvider, new ChargebackStorage.Factory() {
                    @Override
                    public @NonNull ChargebackStorage create(@NonNull User user) {
                        return ChargebackStorage.UsingCSV.excelFriendly(new FileSupplier(new File(getDataFolder(), "chargebacks/" + user.getName() + ".csv")));
                    }
                }))
                .registerFactory(PersistenceService.class, new PersistenceService.RefFactory(Arrays.asList(
                        new SessionObjectStorage.Factory()
                )))
                .build();
        //ChargebackHandler.Factory chargebackHandlerFactory = user -> new ChargebackHandler(serviceRegistry.createReference(ChargebackService.class, user));
        Loc loc = new Loc();
        DobbiePlugin plugin = new DobbiePlugin(
                new Dobbie(
                        new DobbieSettings(config, userSettingsProvider),
                        new Source.Factory.Provider.Immutable(Arrays.asList(
                                new TwitchChatSourceFactory(
                                        twitchInstance,
                                        new ListCancellationHandler(Arrays.asList(
                                                new UserNotifyingCancellationHandler(loc)
                                        )),
                                        userSettingsProvider,
                                        new NameCache(twitchInstance)
                                )
                        )),
                        new SequentalActionFactory(Arrays.asList(
                                new DestActionFactory(
                                        new CmdContextFactory(
                                                new AnnotationBasedObjectContextFactory(
                                                        new ListObjectContextFactory(
                                                                new StorageAwareObjectContext.Factory(
                                                                        ObjectContextFactory.Simple.INSTANCE,
                                                                        PrimitiveMap.Factory.INSTANCE,
                                                                        "storage"
                                                                ),
                                                                Arrays.asList(
                                                                        new TwitchInterface.AsObjectContextInitializer(),
                                                                        serviceRegistry,
                                                                        // TODO more abstract
                                                                        (cb, trigger) -> {
                                                                            cb.set("minecraft", bukkitCompat);
                                                                            cb.set("bukkit", getServer());
                                                                            if (trigger instanceof UserRelatedTrigger) {
                                                                                User user = ((UserRelatedTrigger) trigger).getUser();
                                                                                if (user instanceof BukkitPlayer) {
                                                                                    cb.set("player", user);
                                                                                }
                                                                            }
                                                                        }
                                                                )
                                                        ),
                                                        new SnakeCaseTranslator(
                                                                new TrailingRemovingTranslator(
                                                                        VarNameTranslator.NONE,
                                                                        Collections.singletonList("trigger"),
                                                                        Collections.singletonList("get"),
                                                                        Collections.emptyList()
                                                                )
                                                        ),
                                                        new PrimitiveConverterCache(),
                                                        SequentalConverterProvider.builder()
                                                                .registerProvider(new AnnotationBasedConverterProvider())
                                                                .registerProvider(PrimitiveConverterProvider.builder()
                                                                        .registerStandardConverters()
                                                                        .build()
                                                                )
                                                                .build()
                                                ),
                                                new PlainCmd.Executor() {
                                                    @Override
                                                    public @NonNull CmdResult execute(@NonNull CmdContext context, @NonNull String command) throws CmdExecutionException {
                                                        bukkitCompat.getServer().executeCommand(command);
                                                        return CmdResult.SHOULD_CONTINUE;
                                                    }
                                                }
                                        ),
                                        userSettingsProvider,
                                        config.subscribe(DestMap.class),
                                        loc
                                ),
                                new FallbackActionFactory.Instance(loc)
                        )),
                        new PerUserActionScheduler(new SimpleActionScheduler(ticker, loc), loc),
                        new TriggerErrorHandler.NotifyingUser(loc),
                        serviceRegistry
                ),
                ticker,
                loc
        );
        plugin.start();
        this.dobbiePlugin = plugin;
    }

    @Override
    public void onDisable() {
        dobbiePlugin.cleanup();
        dobbiePlugin = null;
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event) {
        dobbiePlugin.registerUser(new BukkitPlayer(bukkitCompat, event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dobbiePlugin.unregisterUser(new BukkitPlayer(bukkitCompat, event.getPlayer()));
    }
}
