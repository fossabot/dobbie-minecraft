package live.dobbie.core.substitutor.plain;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.VarProvider;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.environment.Environment;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlainSubstitutorParserTest {

    @Test
    void noVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        ListSubstitutable result = parser.parse("hello!");
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("hello!", result.substitute(mock(Env.class)));
    }

    @Test
    void singleVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        ListSubstitutable result = parser.parse("hello, ${something}!");
        assertNotNull(result);
        VarProvider varProvider = mock(VarProvider.class);
        when(varProvider.getVar(eq("something"))).thenReturn("world");
        Env env = new Environment(Collections.singletonMap(VarProvider.class, varProvider));
        assertEquals("hello, world!", result.substitute(env));
    }

    @Test
    void escapeVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        ListSubstitutable result = parser.parse("hello, \\${something}!");
        assertNotNull(result);
        assertEquals("hello, \\${something}!", result.substitute(mock(Env.class)));
    }

    @Test
    void doubleEscapeVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        ListSubstitutable result = parser.parse("hello, \\\\${something}!");
        assertNotNull(result);
        VarProvider varProvider = mock(VarProvider.class);
        when(varProvider.getVar(eq("something"))).thenReturn("world");
        Env env = new Environment(Collections.singletonMap(VarProvider.class, varProvider));
        assertEquals("hello, \\world!", result.substitute(env));
    }

    @Test
    void badVarName() {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        assertThrows(ParserException.class, () -> parser.parse("hello, ${@}!"));
        assertThrows(ParserException.class, () -> parser.parse("hello, ${{}!"));
    }

    @Test
    void varModTest() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser(
                Collections.singletonMap("test", value -> value == null ? null : value.toUpperCase()),
                null
        );
        VarProvider varProvider = mock(VarProvider.class);
        when(varProvider.requireVar(anyString())).thenCallRealMethod();
        when(varProvider.getVar(eq("foo"))).thenReturn("bar");
        Env env = new Environment(Collections.singletonMap(VarProvider.class, varProvider));
        assertEquals("hello, bar", parser.parse("hello, ${foo}").substitute(env));
        assertEquals("hello, BAR", parser.parse("hello, ${*test*foo}").substitute(env));
    }

    @Test
    void badVarModTest() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        assertThrows(ParserException.class, () -> parser.parse("hello, ${*}"));
        assertThrows(ParserException.class, () -> parser.parse("hello, ${**}!"));
        assertThrows(ParserException.class, () -> parser.parse("hello, ${*no_text*}!"));
        assertThrows(ParserException.class, () -> parser.parse("hello, ${**bar}!"));
        assertThrows(ParserException.class, () -> parser.parse("hello, ${*does_not_exist*bar}!"));
    }

}