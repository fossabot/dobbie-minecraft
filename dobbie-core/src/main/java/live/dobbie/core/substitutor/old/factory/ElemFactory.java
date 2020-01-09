package live.dobbie.core.substitutor.old.factory;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import lombok.NonNull;

public interface ElemFactory<El extends Substitutable> {
    El parse(@NonNull Context context) throws ParserException;

    interface Context {
        boolean isMarker(char ch);

        char parseNext();

        Substitutable parseNext(int chars, int ignoredCharsAfter) throws ParserException;

        void rewind(int chars);
    }
}
