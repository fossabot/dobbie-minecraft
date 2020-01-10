package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.path.Path;
import live.dobbie.core.settings.source.ISettingsSectionSource;
import live.dobbie.core.settings.upgrader.SchemaUpgrader;
import live.dobbie.core.util.io.IOSupplier;
import live.dobbie.core.util.io.mod.ModSignal;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JacksonSource implements ISettingsSectionSource<JacksonNode> {
    private static final ILogger LOGGER = Logging.getLogger(JacksonSource.class);

    private final @NonNull ObjectMapper objectMapper;
    private final @NonNull IOSupplier ioSupplier;
    private final SchemaUpgrader upgrader;

    @NonNull
    @Getter(AccessLevel.PACKAGE)
    private JacksonContext context;
    @NonNull
    private JacksonNode node;
    private ModSignal lastModification;

    public JacksonSource(@NonNull ObjectMapper objectMapper, @NonNull IOSupplier supplier, SchemaUpgrader upgrader) {
        this.objectMapper = objectMapper;
        this.ioSupplier = supplier;
        this.upgrader = upgrader;
        init(objectMapper.createObjectNode());
    }

    public JacksonSource(@NonNull ObjectMapper objectMapper, @NonNull IOSupplier supplier) {
        this(objectMapper, supplier, null);
    }

    public JacksonSource(@NonNull IOSupplier supplier, SchemaUpgrader upgrader) {
        this(new ObjectMapper(), supplier, upgrader);
    }

    public JacksonSource(@NonNull IOSupplier supplier) {
        this(supplier, null);
    }

    public JacksonSource(@NonNull IOSupplier supplier, boolean loadImmediately) throws IOException, ParserException {
        this(supplier);
        if (loadImmediately) {
            load();
        }
    }

    private void init(@NonNull ObjectNode objectNode) {
        this.context = new JacksonContext(objectMapper, objectNode);
        this.node = new JacksonNode(context, Path.EMPTY);
    }

    private void runUpgrader() throws ParserException, IOException {
        if (upgrader == null) {
            return;
        }
        if (upgrader.isNotCompatible(this)) {
            upgrader.tryUpgrade(this);
        }
    }

    @NonNull
    @Override
    public JacksonNode getRootSection() {
        return node;
    }

    @Override
    public void load() throws ParserException, IOException {
        if (sourceNotChanged()) {
            return;
        }
        try (InputStreamReader reader = new InputStreamReader(ioSupplier.input(), StandardCharsets.UTF_8)) {
            JsonNode jsonNode = objectMapper.readTree(reader);
            if (jsonNode == null) {
                jsonNode = objectMapper.createObjectNode();
            } else {
                Validate.isTrue(jsonNode instanceof ObjectNode, "expected ObjectNode; got: " + jsonNode.getClass());
            }
            init((ObjectNode) jsonNode);
            runUpgrader();
        } catch (ParserException | JsonProcessingException | RuntimeException e) {
            init(objectMapper.createObjectNode());
            throw new ParserException("could not load " + ioSupplier, e);
        }
    }

    @Override
    public void save() throws IOException, ParserException {
        if (!context.didMod()) {
            return;
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(ioSupplier.output(), StandardCharsets.UTF_8)) {
            objectMapper.writeTree(objectMapper.getFactory().createGenerator(writer), this.context.getRootNode());
        } catch (JsonProcessingException | RuntimeException e) {
            throw new ParserException("could not save " + ioSupplier, e);
        }
        context.modClear();
    }

    private boolean sourceNotChanged() {
        ModSignal latestModification = ioSupplier.getModSignal();
        if (Objects.equals(lastModification, latestModification)) {
            LOGGER.tracing("Not going to reload " + ioSupplier + " because it is not changed: " + latestModification);
            return true;
        }
        lastModification = latestModification;
        return false;
    }
}
