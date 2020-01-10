package live.dobbie.core.script;

import lombok.NonNull;

public interface ScriptExecutor<S extends Script<C>, C extends ScriptContext> {
    @NonNull <T> ScriptResult<T> execute(@NonNull S script, @NonNull C scriptContext, @NonNull Class<T> expectedType) throws ScriptExecutionException;

    @NonNull
    default ScriptResult execute(@NonNull S script, @NonNull C scriptContext) throws ScriptExecutionException {
        return execute(script, scriptContext, Object.class);
    }

    @NonNull
    default <T> T executeAndGet(@NonNull S script, @NonNull C scriptContext, @NonNull Class<T> expectedType) throws ScriptExecutionException {
        ScriptResult<T> result = execute(script, scriptContext, expectedType);
        if (result.isNull()) {
            throw new ScriptExecutionException("expected " + expectedType + ", but got none");
        }
        return result.getObject();
    }

    default boolean executeBoolean(@NonNull S script, @NonNull C scriptContext) throws ScriptExecutionException {
        return executeAndGet(script, scriptContext, Boolean.class);
    }

    default int executeInteger(@NonNull S script, @NonNull C scriptContext) throws ScriptExecutionException {
        return executeAndGet(script, scriptContext, Integer.class);
    }

    default long executeLong(@NonNull S script, @NonNull C scriptContext) throws ScriptExecutionException {
        return executeAndGet(script, scriptContext, Long.class);
    }

    default double executeDouble(@NonNull S script, @NonNull C scriptContext) throws ScriptExecutionException {
        return executeAndGet(script, scriptContext, Double.class);
    }

    @NonNull
    default String executeString(@NonNull S script, @NonNull C scriptContext) throws ScriptExecutionException {
        return executeAndGet(script, scriptContext, String.class);
    }
}
