package live.dobbie.core.substitutor.old.parser;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import live.dobbie.core.substitutor.old.ConstElem;
import live.dobbie.core.substitutor.old.factory.ElemFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ElemParser implements SubstitutableParser {
    private final List<ElemFactory> factoryList;

    public Substitutable parseElement(@NonNull String str) throws ParserException {
        if (str.isEmpty()) {
            return ConstElem.EMPTY;
        }
        return new ElemParserContext(factoryList, str).parse();
    }

    @Override
    public @NonNull Substitutable parse(@NonNull String str) throws ParserException {
        return parseElement(str);
    }
}
