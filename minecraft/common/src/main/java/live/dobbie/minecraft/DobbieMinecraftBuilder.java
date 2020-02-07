package live.dobbie.minecraft;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import live.dobbie.core.Dobbie;
import live.dobbie.core.DobbieSettings;
import live.dobbie.core.action.factory.FallbackActionFactory;
import live.dobbie.core.action.factory.SequentalActionFactory;
import live.dobbie.core.action.scheduler.PerUserActionScheduler;
import live.dobbie.core.action.scheduler.SimpleActionScheduler;
import live.dobbie.core.config.DobbieLocale;
import live.dobbie.core.config.PriceFormatting;
import live.dobbie.core.context.factory.AnnotationBasedObjectContextFactory;
import live.dobbie.core.context.factory.ObjectContextFactory;
import live.dobbie.core.context.factory.list.ListObjectContextFactory;
import live.dobbie.core.context.factory.list.ObjectContextInitializer;
import live.dobbie.core.context.factory.nametranslator.SnakeCaseTranslator;
import live.dobbie.core.context.factory.nametranslator.TrailingRemovingTranslator;
import live.dobbie.core.context.factory.nametranslator.VarNameTranslator;
import live.dobbie.core.context.storage.PrimitiveMap;
import live.dobbie.core.context.storage.StorageAwareObjectContext;
import live.dobbie.core.context.value.ContextualCondition;
import live.dobbie.core.context.value.ScriptContextualValue;
import live.dobbie.core.dest.DestActionFactory;
import live.dobbie.core.dest.DestMap;
import live.dobbie.core.dest.DestSectionLocator;
import live.dobbie.core.dest.cmd.*;
import live.dobbie.core.dest.cmd.script.AssertionScriptCmdParser;
import live.dobbie.core.dest.cmd.script.ConditionalScriptCmdParser;
import live.dobbie.core.dest.cmd.script.ScriptCmdParser;
import live.dobbie.core.dictionary.sql.PlainSQLDictionaryDatabaseAdapter;
import live.dobbie.core.dictionary.sql.SQLDictionaryDatabaseFactory;
import live.dobbie.core.dictionary.sql.h2.H2;
import live.dobbie.core.dictionary.sql.h2.H2DictionaryDatabaseInitializer;
import live.dobbie.core.dictionary.sql.pool.hikari.HikariConnectionPoolFactory;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.misc.Price;
import live.dobbie.core.misc.currency.ICUCurrencyFormatter;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.misc.primitive.StringPrimitive;
import live.dobbie.core.misc.primitive.converter.AnnotationBasedConverterProvider;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverterCache;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverterProvider;
import live.dobbie.core.misc.primitive.converter.SequentalConverterProvider;
import live.dobbie.core.path.Path;
import live.dobbie.core.persistence.OnDemandPersistence;
import live.dobbie.core.persistence.PersistenceService;
import live.dobbie.core.persistence.PrimitiveDictionaryPersistence;
import live.dobbie.core.persistence.SessionObjectStorage;
import live.dobbie.core.plugin.DobbiePlugin;
import live.dobbie.core.plugin.ticker.ScheduledThreadPoolTicker;
import live.dobbie.core.plugin.ticker.Ticker;
import live.dobbie.core.script.js.JSModuleScriptProvider;
import live.dobbie.core.script.js.JSScriptCompiler;
import live.dobbie.core.script.js.JSScriptContext;
import live.dobbie.core.script.js.JSScriptExecutor;
import live.dobbie.core.script.js.converter.*;
import live.dobbie.core.script.js.moduleprovider.DirectoryModuleProvider;
import live.dobbie.core.script.js.moduleprovider.URIAwareModuleProvider;
import live.dobbie.core.service.Service;
import live.dobbie.core.service.ServiceRef;
import live.dobbie.core.service.ServiceRegistry;
import live.dobbie.core.service.chargeback.ChargebackService;
import live.dobbie.core.service.chargeback.ChargebackStorage;
import live.dobbie.core.service.scheduler.IdSchedulerService;
import live.dobbie.core.service.scheduler.IdTaskScheduledCmd;
import live.dobbie.core.service.scheduler.IdTaskScheduler;
import live.dobbie.core.service.streamelements.StreamElementsSourceFactory;
import live.dobbie.core.service.streamlabs.StreamLabsSourceFactory;
import live.dobbie.core.service.streamlabs.api.StreamLabsApi;
import live.dobbie.core.service.twitch.*;
import live.dobbie.core.settings.ISettings;
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
import live.dobbie.core.trigger.priced.Donated;
import live.dobbie.core.trigger.priced.Priced;
import live.dobbie.core.user.SimpleUserSettingsProvider;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserNotifyingCancellationHandler;
import live.dobbie.core.util.io.FileSupplier;
import live.dobbie.minecraft.compat.MinecraftCompat;
import live.dobbie.minecraft.compat.MinecraftOnlinePlayer;
import live.dobbie.minecraft.dest.cmd.ExecuteAtPlayer;
import live.dobbie.minecraft.dest.cmd.ExecutePlayer;
import live.dobbie.minecraft.dest.cmd.SendRawCmd;
import lombok.NonNull;
import org.mozilla.javascript.ContextFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public class DobbieMinecraftBuilder {

    public static DobbiePlugin create(@NonNull String brand,
                                      @NonNull File configDir,
                                      @NonNull Supplier<MinecraftCompat> compatSupplier,
                                      @NonNull Map<Class<? extends Service>, ServiceRef.Factory> serviceFactories,
                                      ObjectContextInitializer customContextInitializer) {
        return create(brand, configDir, compatSupplier, new ScheduledThreadPoolTicker(), serviceFactories, customContextInitializer);
    }

    public static DobbiePlugin create(@NonNull String brand,
                                      @NonNull File configDir,
                                      @NonNull Supplier<MinecraftCompat> compatSupplier,
                                      @NonNull Ticker ticker,
                                      @NonNull Map<Class<? extends Service>, ServiceRef.Factory> serviceFactories,
                                      ObjectContextInitializer customContextInitializer) {
        ICUCurrencyFormatter.setFactory(new MinecraftCurrencyFormatter.Factory(ICUCurrencyFormatter.getFactory()));

        File configFile = new File(configDir, "config.yaml");
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
                new JSModuleScriptProvider(
                        new URIAwareModuleProvider(
                                new DirectoryModuleProvider(new File(configDir, "js_modules"))
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
                                .register(new V0Upgrader(brand, "To receive help please visit https://dobbie.live/"))
                                .build()
                ),
                parserProvider
        );
        config.refreshValues(); // it is recommended to run it before the first tick
        Loc loc = new Loc();
        TwitchInstance twitchInstance = new TwitchInstance(config);
        SimpleUserSettingsProvider<JacksonNode> userSettingsProvider = new SimpleUserSettingsProvider<>(
                new JacksonSourceProvider(
                        o,
                        null,
                        new DirectoryBasedFileSourceFactory(new File(configDir, "players"), ".yaml")
                ),
                parserProvider
        );
        PlainSubstitutorParser plainSubstitutorParser = PlainSubstitutorParser.builder()
                .defaultVarMod(VarConverter.JsonEscaping.INSTANCE)
                .varMod("de", VarConverter.DoubleJsonEscaping.INSTANCE)
                .varMod("raw", VarConverter.Identity.INSTANCE)
                .build();
        ServiceRegistry.Builder serviceRegistryBuilder = ServiceRegistry.builder()
                .registerFactory(ChargebackService.class, new ChargebackService.RefFactory(userSettingsProvider, new ChargebackStorage.Factory() {
                    @Override
                    public @NonNull ChargebackStorage create(@NonNull User user) {
                        return ChargebackStorage.UsingCSV.excelFriendly(new FileSupplier(new File(configDir, "chargebacks/" + user.getName() + ".csv")));
                    }
                }))
                .registerFactory(PersistenceService.class, new PersistenceService.RefFactory(Arrays.asList(
                        new SessionObjectStorage.Factory(),
                        new OnDemandPersistence.Factory("sql", new PrimitiveDictionaryPersistence.FactoryDelegated(
                                new SQLDictionaryDatabaseFactory(
                                        user -> H2.file(new File(configDir, "db/" + user.getName())).build(),
                                        new H2DictionaryDatabaseInitializer(),
                                        new PlainSQLDictionaryDatabaseAdapter(),
                                        new HikariConnectionPoolFactory()
                                ),
                                "sql"
                        ))
                )))
                .registerFactory(IdTaskScheduler.class, new IdSchedulerService.RefFactory())
                .registerFactory(StreamLabsApi.class, new StreamLabsApi.RefFactory(userSettingsProvider));
        serviceFactories.forEach(serviceRegistryBuilder::registerFactory);
        ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
        sequentalCmdParser.registerParser(
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("execute_after"), new IdTaskScheduledCmd.ExecuteAfter.Parser(serviceRegistry, sequentalCmdParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("run_later"), new IdTaskScheduledCmd.RunLater.Parser(serviceRegistry, sequentalCmdParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("repeat_every"), new IdTaskScheduledCmd.RepeatEvery.Parser(serviceRegistry, plainSubstitutorParser, sequentalCmdParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("cancel"), new IdTaskScheduledCmd.CancelTask.Parser(serviceRegistry, plainSubstitutorParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("send"), new SendCmd.Parser(plainSubstitutorParser, false)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("send_raw"), new SendRawCmd.Parser(plainSubstitutorParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("error"), new SendCmd.Parser(plainSubstitutorParser, true)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("wait"), new WaitCmd.Parser(plainSubstitutorParser, WaitCmd.WaitStrategy.DEFAULT)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("player"), new ExecutePlayer.Parser(plainSubstitutorParser)),
                AbstractPatternCmdParser.NameAware.wrap(Arrays.asList("at_player"), new ExecuteAtPlayer.Parser(plainSubstitutorParser, new ExecuteAtPlayer.ConsoleExecuteAt(() -> compatSupplier.get().getServer()))),
                new ReplyChatCmd.Parser(Arrays.asList("twitch_reply", "reply"), plainSubstitutorParser),
                new ConditionalScriptCmdParser<>(
                        Arrays.asList("javascript_if", "js_if"),
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
        //ChargebackHandler.Factory chargebackHandlerFactory = user -> new ChargebackHandler(serviceRegistry.createReference(ChargebackService.class, user));
        ListCancellationHandler cancellationHandler = new ListCancellationHandler(Arrays.asList(
                new UserNotifyingCancellationHandler(loc)
        ));
        NameCache nameCache = new NameCache(twitchInstance);
        DobbiePlugin plugin = new DobbiePlugin(
                new Dobbie(
                        new DobbieSettings(config, userSettingsProvider),
                        new Source.Factory.Provider.Immutable(Arrays.asList(
                                new TwitchSourceFactory(
                                        twitchInstance,
                                        cancellationHandler,
                                        userSettingsProvider,
                                        nameCache,
                                        new ChannelOnlineObserver(twitchInstance, nameCache, new GameCache(twitchInstance), 5000, 30000)
                                ),
                                new StreamLabsSourceFactory(
                                        serviceRegistry,
                                        cancellationHandler,
                                        userSettingsProvider,
                                        loc
                                ),
                                new StreamElementsSourceFactory(
                                        userSettingsProvider,
                                        loc,
                                        cancellationHandler
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
                                                                        (cb, trigger) -> {
                                                                            MinecraftCompat compat = compatSupplier.get();
                                                                            cb.set("minecraft", compat);
                                                                            if (trigger instanceof UserRelatedTrigger) {
                                                                                User user = ((UserRelatedTrigger) trigger).getUser();
                                                                                if (user instanceof MinecraftOnlinePlayer) {
                                                                                    cb.set("player", user);
                                                                                }
                                                                                if (trigger instanceof Priced) {
                                                                                    ISettings userSettings = userSettingsProvider.get(user);
                                                                                    DobbieLocale locale = userSettings.getValue(DobbieLocale.class);
                                                                                    if (locale == null) {
                                                                                        locale = config.getValue(DobbieLocale.class);
                                                                                        if (locale == null) {
                                                                                            locale = DobbieLocale.BY_DEFAULT;
                                                                                        }
                                                                                    }
                                                                                    Price price = ((Priced) trigger).getPrice();
                                                                                    StringPrimitive formattedPrice = Primitive.of(PriceFormatting.format(price, locale, userSettings.getValue(PriceFormatting.class)));
                                                                                    cb.set(Path.of("price", "formatted"), formattedPrice);
                                                                                    if (trigger instanceof Donated) {
                                                                                        cb.set(Path.of("donation", "formatted"), formattedPrice);
                                                                                    }
                                                                                }
                                                                            }
                                                                            if (customContextInitializer != null) {
                                                                                customContextInitializer.initialize(cb, trigger);
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
                                                        compatSupplier.get().getServer().executeCommand(command);
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
        return plugin;
    }
}
