package live.dobbie.core.dest.cmd;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import lombok.NonNull;


public interface CmdParser {
    Cmd parse(@NonNull Text text) throws ParserException;

    default Cmd parse(@NonNull String str) throws ParserException {
        return parse(Text.ofUnknown(str));
    }
}
