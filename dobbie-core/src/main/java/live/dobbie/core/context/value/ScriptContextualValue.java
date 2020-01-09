package live.dobbie.core.context.value;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.exception.ComputationException;
import live.dobbie.core.script.*;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
@EqualsAndHashCode(of = {"source", "valueClass"})
public class ScriptContextualValue<V, S extends Script<C>, C extends ScriptContext> implements ContextualValue<V> {
    private final @NonNull ScriptContext.Factory<C> contextFactory;
    private final @NonNull ScriptExecutor<S, C> executor;
    private final @NonNull ScriptCompiler<S> compiler;
    private final @NonNull ScriptSource source;
    private final @NonNull Class<V> valueClass;

    private S script;
    private Exception compilationException;

    private void compileScript() throws ComputationException {
        if (compilationException != null) {
            throw new ComputationException("script compilation failed previously", compilationException);
        }
        try {
            script = compiler.compile(source);
        } catch (ScriptCompilationException | IOException e) {
            compilationException = e;
            throw new ComputationException("could not compile script", e);
        }
    }

    private V executeScript(ObjectContext context) throws ComputationException {
        try {
            return executor.executeAndGet(script, contextFactory.create(context), valueClass);
        } catch (ScriptExecutionException e) {
            throw new ComputationException(e);
        }
    }

    @NonNull
    @Override
    public V computeValue(@NonNull ObjectContext context) throws ComputationException {
        compileScript();
        return executeScript(context);
    }

    @RequiredArgsConstructor
    public static class Factory<S extends Script<C>, C extends ScriptContext> {
        private final @NonNull ScriptContext.Factory<C> contextFactory;
        private final @NonNull ScriptExecutor<S, C> executor;
        private final @NonNull ScriptCompiler<S> compiler;

        public <V> ScriptContextualValue<V, S, C> create(@NonNull String script,
                                                         @NonNull String scriptSourceName,
                                                         int scriptSourceLine,
                                                         @NonNull Class<V> valueClass) {
            return new ScriptContextualValue<>(
                    contextFactory,
                    executor,
                    compiler,
                    ScriptSource.fromString(script, scriptSourceName, scriptSourceLine),
                    valueClass
            );
        }
    }

    @RequiredArgsConstructor
    public static class Parser<V, S extends Script<C>, C extends ScriptContext> extends JsonDeserializer<ScriptContextualValue<V, S, C>> {
        private final @NonNull Factory<S, C> factory;
        private final @NonNull String sourceName;
        private final @NonNull Class<V> valueClass;

        @Override
        public ScriptContextualValue<V, S, C> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonLocation location = p.getCurrentLocation();
            ObjectCodec codec = p.getCodec();
            String script = codec.readValue(p, String.class);
            return factory.create(script, sourceName, location.getLineNr(), valueClass);
        }

        public static <S extends Script<C>, C extends ScriptContext> Parser<?, S, C> of(
                @NonNull Factory<S, C> factory,
                @NonNull String sourceName) {
            return new Parser<>(factory, sourceName, Object.class);
        }
    }
}
