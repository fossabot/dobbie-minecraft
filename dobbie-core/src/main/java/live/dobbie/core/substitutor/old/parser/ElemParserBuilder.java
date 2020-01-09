package live.dobbie.core.substitutor.old.parser;

import live.dobbie.core.substitutor.old.factory.ElemFactory;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class ElemParserBuilder {
    /*private static final char[] PLAIN_MARKER = new char[]{'@'};
    private static final char[] JSON_SAFE_MARKER = new char[]{'$'};
    private static final String JSON_SAFE_FUNCTION_NAME = "json";
    private static final char ARGUMENT_SEPARATOR = ',';*/
    private final ArrayList<ElemFactory> elementFactories = new ArrayList<>();

    public ElemParserBuilder register(@NonNull ElemFactory factory) {
        elementFactories.add(factory);
        return this;
    }

    public ElemParserBuilder register(@NonNull Collection<ElemFactory> factories) {
        elementFactories.addAll(factories);
        return this;
    }

    public ElemParser build() {
        return new ElemParser(elementFactories);
    }
}
