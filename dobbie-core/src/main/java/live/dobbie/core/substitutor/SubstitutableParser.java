package live.dobbie.core.substitutor;

import live.dobbie.core.exception.ParserException;
import lombok.NonNull;

public interface SubstitutableParser {
    @NonNull Substitutable parse(@NonNull String str) throws ParserException;
}
