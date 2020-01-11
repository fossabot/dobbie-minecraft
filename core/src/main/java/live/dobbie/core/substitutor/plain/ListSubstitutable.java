package live.dobbie.core.substitutor.plain;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class ListSubstitutable implements Substitutable {
    @Getter(AccessLevel.PACKAGE)
    private final List<Substitutable> list;

    @Override
    public @NonNull String substitute(@NonNull Env env) {
        return list.stream().map(s -> s.substitute(env)).collect(Collectors.joining());
    }
}
