package live.dobbie.core.substitutor.old.factory;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.old.func.FuncElem;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class FunctionAbstractElemFactory<El extends Substitutable> extends MarkerAbstractElemFactory<El> {
    private static final char DEFAULT_SEPARATOR_CHAR = ',';

    protected final @NonNull FuncElem.Func function;
    private final int minArguments, maxArguments;
    private final char separatorChar;

    public FunctionAbstractElemFactory(@NonNull String markerName, @NonNull FuncElem.Func function,
                                       int minArguments, int maxArguments, char separatorChar) {
        super(markerName);
        if (minArguments < 1) {
            throw new IllegalArgumentException("min < 1");
        }
        if (minArguments > maxArguments) {
            throw new IllegalArgumentException("min > max");
        }
        this.function = function;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.separatorChar = separatorChar;
    }

    public FunctionAbstractElemFactory(@NonNull String markerName, @NonNull FuncElem.Func function,
                                       int minArguments, int maxArguments) {
        this(markerName, function, minArguments, maxArguments, DEFAULT_SEPARATOR_CHAR);
    }

    public FunctionAbstractElemFactory(@NonNull String markerName, @NonNull FuncElem.Func function) {
        this(markerName, function, 1, 1, '\0');
    }

    @Override
    protected El parseAfterMarkerName(String markerName, Context context) throws ParserException {
        if (!findOpeningBrackets(context)) {
            return null;
        }
        if (maxArguments == 1) {
            int charsUntilClosingBrackets = lookForClosingBrackets(context);
            Substitutable enclosingBlock = parseEnclosingBlock(context, charsUntilClosingBrackets);
            return createFunctionElement(markerName, Collections.singletonList(enclosingBlock));
        } else {
            List<Substitutable> elements = new ArrayList<>();
            int charsToParse = lookForEnclosedElementsAndClosingBrackets(elements, context);
            addLastRemainingElement(elements, charsToParse, context);
            checkArgumentsCount(markerName, elements);
            return createFunctionElement(markerName, elements);
        }
    }

    protected int lookForEnclosedElementsAndClosingBrackets(List<Substitutable> elements, Context context) throws ParserException {
        char ch;
        int skipTimes = 0, charsToParse = 0;
        lookingForArgumentSeparatorAndClosingBrackets:
        {
            loop:
            while (true) {
                ch = context.parseNext();
                switch (ch) {
                    case 0:
                        throw new ParserException("missing closing brackets");
                    case '}':
                        if (skipTimes-- == 0) {
                            break lookingForArgumentSeparatorAndClosingBrackets;
                        }
                        charsToParse++;
                        continue loop;
                    case '{':
                        skipTimes++;
                        charsToParse++;
                        continue loop;
                }
                if (ch == separatorChar) {
                    if (skipTimes == 0) {
                        Substitutable element = context.parseNext(charsToParse, 1);
                        elements.add(element);
                        charsToParse = 0;
                        continue loop;
                    }
                }
                charsToParse++;
            }
        }
        return charsToParse;
    }

    protected int lookForClosingBrackets(Context context) throws ParserException {
        char ch;
        int charsUntilClosingBrackets = 0;
        int skipTimes = 0;
        lookingForClosingBrackets:
        {
            while (true) {
                ch = context.parseNext();
                switch (ch) {
                    case 0:
                        throw new ParserException("Missing closing brackets");
                    case '}':
                        if (skipTimes-- == 0) {
                            break lookingForClosingBrackets;
                        }
                        charsUntilClosingBrackets++;
                        break;
                    case '{':
                        skipTimes++;
                    default:
                        charsUntilClosingBrackets++;
                        break;
                }
            }
        }
        return charsUntilClosingBrackets;
    }

    protected void addLastRemainingElement(List<Substitutable> elements, int charsToParse, Context context) throws ParserException {
        Substitutable lastElement = context.parseNext(charsToParse, 1);
        elements.add(lastElement);
    }

    protected void checkArgumentsCount(String markerName, List<Substitutable> elements) throws ParserException {
        int size = elements.size();
        if (size < minArguments) {
            throw new ParserException("not enough arguments for function " + markerName);
        }
        if (size > maxArguments) {
            throw new ParserException("too many argument for function " + markerName);
        }
    }

    protected Substitutable parseEnclosingBlock(Context context, int charsUntilClosingBrackets) throws ParserException {
        return context.parseNext(charsUntilClosingBrackets, 1);
    }

    protected abstract El createFunctionElement(String markerName, List<Substitutable> enclosingBlocks);
}
