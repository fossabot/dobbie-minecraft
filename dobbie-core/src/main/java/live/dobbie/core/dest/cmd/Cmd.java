package live.dobbie.core.dest.cmd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.exception.ParserRuntimeException;
import live.dobbie.core.misc.Text;
import live.dobbie.core.misc.TextLocation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.Collection;

import static live.dobbie.core.dest.cmd.CmdResult.SHOULD_CONTINUE;

public interface Cmd {
    @NonNull CmdResult execute(@NonNull CmdContext context) throws CmdExecutionException;

    static CmdResult executeFrom(@NonNull Collection<Cmd> collection, @NonNull CmdContext context) throws CmdExecutionException {
        CmdResult result = SHOULD_CONTINUE;
        cmdLoop:
        for (Cmd cmd : collection) {
            Validate.notNull(cmd, "one of Cmd in collection");
            result = cmd.execute(context);
            switch (result) {
                case SHOULD_CONTINUE:
                    continue cmdLoop;
                case SHOULD_STOP:
                    break cmdLoop;
            }
        }
        return result;
    }

    @RequiredArgsConstructor
    class JacksonParser extends JsonDeserializer<Cmd> {
        private final @NonNull CmdParser parser;

        @Override
        public Cmd deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            String str = codec.readValue(p, String.class);
            Text text = new Text(str, TextLocation.of(p.getCurrentLocation()));
            try {
                return parser.parse(text);
            } catch (ParserException e) {
                throw new ParserRuntimeException("could not parse Cmd at " + text.getAt(), e);
            }
        }
    }

    @NonNull
    static <T> T notNull(T object, String name) throws CmdExecutionException {
        if (object == null) {
            throw new CmdExecutionException(new NullPointerException(name));
        }
        return object;
    }
}
