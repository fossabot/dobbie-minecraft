package live.dobbie.core.substitutor.old.factory;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;


public abstract class VarAbstractElemFactory<El extends Substitutable> extends MarkerAbstractElemFactory<El> {
    public VarAbstractElemFactory(String markerName) {
        super(markerName);
    }

    @Override
    protected final El parseAfterMarkerName(String markerName, Context context) throws ParserException {
        char ch = context.parseNext();
        switch (ch) {
            case '{':
                return null;
            case 0:
                break;
            default:
                context.rewind(1);
        }
        return createElem(markerName);
    }

    protected abstract El createElem(String markerName);
}
