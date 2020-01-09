package live.dobbie.core.substitutor.old.parser;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.old.ConstElem;
import live.dobbie.core.substitutor.old.ContainerElem;
import live.dobbie.core.substitutor.old.factory.ElemFactory;
import live.dobbie.core.substitutor.old.factory.MarkerAbstractElemFactory;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ElemParserContext implements ElemFactory.Context {
    private final List<ElemFactory> elementFactories;
    private final @NonNull String str;
    private final int startIndex, endIndex;
    private int index;

    ElemParserContext(@NonNull List<ElemFactory> factories, @NonNull String str, int startIndex, int endIndex) {
        if (startIndex < 0) {
            throw new IllegalArgumentException("startIndex < 0");
        }
        if (endIndex > str.length() - 1) {
            throw new IllegalArgumentException("endIndex > length - 1");
        }
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("startIndex > endIndex");
        }
        this.elementFactories = factories;
        this.str = str;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.index = startIndex;
    }

    ElemParserContext(@NonNull List<ElemFactory> factories, @NonNull String str) {
        this(factories, str, 0, str.length() - 1);
    }

    Substitutable parse() throws ParserException {
        ArrayList<Substitutable> elements = new ArrayList<>();
        int prevIndex;
        boolean foundAnything;
        StringBuilder b = new StringBuilder();
        while (index <= endIndex) {
            foundAnything = false;
            for (ElemFactory factory : elementFactories) {
                prevIndex = index;
                Substitutable element;

                try {
                    element = factory.parse(this);
                } catch (ParserException parserException) {
                    throw new ParserException("Cannot parse segment \"" + str.substring(startIndex, endIndex) + "\" at index " + index + " (" +
                            ((index > endIndex ? "index overflown or parsing completed" : index < startIndex ? "index underflow" : str.charAt(index))) + ")", parserException);
                }

                if (element != null) {
                    if (b.length() > 0) {
                        elements.add(new ConstElem(b.toString()));
                        b.setLength(0);
                    }
                    elements.add(element);
                    foundAnything = true;
                } else {
                    index = prevIndex;
                }
            }
            if (!foundAnything) {
                b.append(str.charAt(index));
                index++;
            }
        }
        if (b.length() > 0) {
            elements.add(new ConstElem(b.toString()));
        }
        switch (elements.size()) {
            case 0:
                return ConstElem.EMPTY;
            case 1:
                return elements.get(0);
            default:
                return new ContainerElem(elements);
        }
    }

    @Override
    public void rewind(int chars) {
        index -= chars;
    }

    @Override
    public boolean isMarker(char ch) {
        // TODO support markers
        /*boolean foundMarker = false;
        for (char marker : markers) {
            if(ch == marker) {
                foundMarker = true;
                break;
            }
        }*/
        return ch == MarkerAbstractElemFactory.MARKER;//foundMarker;
    }

    @Override
    public char parseNext() {
        if (index > endIndex) {
            return 0;
        }
        return str.charAt(index++);
    }

    @Override
    public Substitutable parseNext(int chars, int ignoredCharsAfter) throws ParserException {
        index -= chars;

        ElemParserContext subContext = new ElemParserContext(elementFactories, str, index - 1, index + chars - 2);
        Substitutable element = subContext.parse();

        index = subContext.index;
        index += ignoredCharsAfter;

        return element;
    }
}
