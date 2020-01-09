package live.dobbie.core.substitutor.old;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class ContainerElem implements Substitutable {
    @NonNull List<Substitutable> children;

    @NonNull
    @Override
    public String substitute(@NonNull Env env) {
        StringBuilder b = new StringBuilder();
        children.forEach(subElem -> b.append(subElem.substitute(env)));
        return b.toString();
    }
}
