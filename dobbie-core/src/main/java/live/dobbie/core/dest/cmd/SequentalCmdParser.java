package live.dobbie.core.dest.cmd;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.Text;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequentalCmdParser implements CmdParser {
    private final List<CmdParser> parsers = new ArrayList<>();

    public SequentalCmdParser(@NonNull CmdParser... parsers) {
        registerParser(parsers);
    }

    public void registerParser(@NonNull CmdParser... parsers) {
        Validate.noNullElements(parsers);
        this.parsers.addAll(Arrays.asList(parsers));
    }

    @Override
    public Cmd parse(@NonNull Text text) throws ParserException {
        Cmd cmd = null;
        for (CmdParser parser : parsers) {
            cmd = parser.parse(text);
            if (cmd != null) {
                break;
            }
        }
        return cmd;
    }
}
