package live.dobbie.core.substitutor.old.factory;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;


@RequiredArgsConstructor
public abstract class MarkerAbstractElemFactory<El extends Substitutable> implements ElemFactory<El> {
    public static final char MARKER = '$';

    private final String markerName;

    @Override
    public final El parse(Context context) throws ParserException {
        if (!context.isMarker(context.parseNext())) {
            return null;
        }
        String processingMarkerName;
        if (markerName != null) {
            if (parseMarkerName(context)) {
                processingMarkerName = markerName;
            } else {
                processingMarkerName = null;
            }
        } else {
            // special case for AnyVarElem
            processingMarkerName = parseAnyMarker(context);
        }
        if (processingMarkerName == null) {
            return null;
        }
        return parseAfterMarkerName(processingMarkerName, context);
    }

    private boolean parseMarkerName(Context context) {
        Validate.notNull(markerName, "markerName");
        for (int i = 0; i < markerName.length(); i++) {
            if (context.parseNext() == markerName.charAt(i)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private String parseAnyMarker(Context context) {
        Validate.isTrue(markerName == null, "markerName must be null");
        StringBuilder markerName = new StringBuilder();
        char ch;
        readLoop:
        while ((ch = context.parseNext()) != '\0') {
            switch (ch) {
                case '_':
                case '.':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    markerName.append(ch);
                    break;
                default:
                    context.rewind(1);
                    break readLoop;
            }
        }
        return markerName.length() == 0 ? null : markerName.toString();
    }

    protected boolean findOpeningBrackets(Context context) {
        return context.parseNext() == '{';
    }

    protected abstract El parseAfterMarkerName(String markerName, Context context) throws ParserException;
}
