package live.dobbie.core.dest.cmd;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.misc.TextLocation;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class AbstractPatternCmdParserTest {

    @Test
    void noArgTest() throws ParserException {
        AbstractPatternCmdParser parser = Mockito.spy(new AbstractPatternCmdParser() {
            @Override
            protected Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException {
                return null;
            }
        });
        parser.parse("#!foo");
        verify(parser).parse(eq("foo"), eq(null), notNull());
    }

    @Test
    void argTest() throws ParserException {
        AbstractPatternCmdParser parser = Mockito.spy(new AbstractPatternCmdParser() {
            @Override
            protected Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException {
                return null;
            }
        });
        parser.parse("#!foo bar");
        verify(parser).parse(eq("foo"), eq("bar"), notNull());
    }

    @Test
    void moreArgTest() throws ParserException {
        AbstractPatternCmdParser parser = Mockito.spy(new AbstractPatternCmdParser() {
            @Override
            protected Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException {
                return null;
            }
        });
        parser.parse("#!foo bar0 bar1 bar2");
        verify(parser).parse(eq("foo"), eq("bar0 bar1 bar2"), notNull());
    }

    @Test
    void noNameFailTest() throws ParserException {
        AbstractPatternCmdParser parser = Mockito.spy(new AbstractPatternCmdParser() {
            @Override
            protected Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException {
                return null;
            }
        });
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#! bar"));
    }

    @Test
    void onlyShebangFailTest() throws ParserException {
        AbstractPatternCmdParser parser = Mockito.spy(new AbstractPatternCmdParser() {
            @Override
            protected Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException {
                return null;
            }
        });
        Assertions.assertThrows(ParserException.class, () -> parser.parse("#!"));
    }

    @Test
    void singleAcceptableNameTest() throws ParserException {
        AbstractPatternCmdParser.NameAware parser = Mockito.spy(new AbstractPatternCmdParser.NameAware(Collections.singletonList("foo")) {
            @Override
            protected Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
                return Mockito.mock(Cmd.class);
            }
        });
        assertNull(parser.parse("#!test"));
        assertNotNull(parser.parse("#!foo"));
    }

    @Test
    void moreAcceptableNameTest() throws ParserException {
        AbstractPatternCmdParser.NameAware parser = Mockito.spy(new AbstractPatternCmdParser.NameAware(Arrays.asList("foo", "bar")) {
            @Override
            protected Cmd createCmd(String args, @NonNull TextLocation location) throws ParserException {
                return Mockito.mock(Cmd.class);
            }
        });
        assertNull(parser.parse("#!test"));
        assertNotNull(parser.parse("#!foo"));
        assertNotNull(parser.parse("#!bar"));
    }

    @Test
    void multilineTest() throws ParserException {
        AbstractPatternCmdParser parser = spy(new AbstractPatternCmdParser() {
            @Override
            protected Cmd parse(@NonNull String name, String args, @NonNull TextLocation location) throws ParserException {
                return mock(Cmd.class);
            }
        });
        assertNotNull(parser.parse("#!test hello,\nmultiline world!"));
        verify(parser).parse(eq("test"), eq("hello,\nmultiline world!"), notNull());
    }

}