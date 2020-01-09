package live.dobbie.core.substitutor.old.func;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class FuncElem implements Substitutable {
    @NonNull Func func;

    @NonNull
    @Override
    public String substitute(@NonNull Env env) {
        return func.execute(env, arguments(env));
    }

    protected abstract List<String> arguments(@NonNull Env env);

    public interface Func {
        @NonNull String execute(@NonNull Env env, @NonNull List<String> arguments);
    }

    public interface ArgFunc extends Func {
        @NonNull String execute(@NonNull Env env, @NonNull String argument);

        @Override
        @NonNull
        default String execute(@NonNull Env env, @NonNull List<String> arguments) {
            return execute(env, StringUtils.join(arguments, ""));
        }
    }
}
