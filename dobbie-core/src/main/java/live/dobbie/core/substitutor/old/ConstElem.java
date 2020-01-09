package live.dobbie.core.substitutor.old;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import lombok.NonNull;
import lombok.Value;


@Value
public class ConstElem implements Substitutable {
    @NonNull String value;

    @NonNull
    @Override
    public String substitute(@NonNull Env env) {
        return value;
    }

    public static final ConstElem EMPTY = new ConstElem("");
}
