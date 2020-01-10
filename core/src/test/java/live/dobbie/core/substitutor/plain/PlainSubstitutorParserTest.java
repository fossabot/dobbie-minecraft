package live.dobbie.core.substitutor.plain;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.environment.Environment;
import live.dobbie.core.substitutor.old.var.AnyVarElem;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlainSubstitutorParserTest {

    @Test
    void noVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        PlainSubstitutable result = parser.parse("hello!");
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("hello!", result.substitute(mock(Env.class)));
    }

    @Test
    void singleVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        PlainSubstitutable result = parser.parse("hello, ${something}!");
        assertNotNull(result);
        AnyVarElem.Provider varProvider = mock(AnyVarElem.Provider.class);
        when(varProvider.getVar(eq("something"))).thenReturn("world");
        Env env = new Environment(Collections.singletonMap(AnyVarElem.Provider.class, varProvider));
        assertEquals("hello, world!", result.substitute(env));
    }

    @Test
    void escapeVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        PlainSubstitutable result = parser.parse("hello, \\${something}!");
        assertNotNull(result);
        assertEquals("hello, \\${something}!", result.substitute(mock(Env.class)));
    }

    @Test
    void doubleEscapeVar() throws ParserException {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        PlainSubstitutable result = parser.parse("hello, \\\\${something}!");
        assertNotNull(result);
        AnyVarElem.Provider varProvider = mock(AnyVarElem.Provider.class);
        when(varProvider.getVar(eq("something"))).thenReturn("world");
        Env env = new Environment(Collections.singletonMap(AnyVarElem.Provider.class, varProvider));
        assertEquals("hello, \\world!", result.substitute(env));
    }

    @Test
    void badVarName() {
        PlainSubstitutorParser parser = new PlainSubstitutorParser();
        assertThrows(ParserException.class, () -> parser.parse("hello, ${**}!"));
        assertThrows(ParserException.class, () -> parser.parse("hello, ${@}!"));
        assertThrows(ParserException.class, () -> parser.parse("hello, ${{}!"));
    }

}