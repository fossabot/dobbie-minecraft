package live.dobbie.core.substitutor.old;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.old.parser.ElemParser;
import live.dobbie.core.substitutor.old.var.AnyVarElem;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnyVarElemTest {

    @Test
    void providerTest() throws ParserException {
        ElemParser parser = new ElemParser(Collections.singletonList(new AnyVarElem.Factory()));
        Substitutable elem = parser.parseElement("hello, $foo");
        AnyVarElem.Provider varProvider = Mockito.mock(AnyVarElem.Provider.class);
        when(varProvider.getVar(eq("foo"))).thenReturn("world");
        when(varProvider.requireVar(notNull())).thenCallRealMethod();
        Env env = Mockito.mock(Env.class);
        when(env.require(any())).thenCallRealMethod();
        when(env.get(eq(AnyVarElem.Provider.class))).thenReturn(varProvider);
        String value = elem.substitute(env);
        assertEquals("hello, world", value);
    }

    @Test
    void varSpaceTest() throws ParserException {
        AnyVarElem.Provider varProvider = Mockito.mock(AnyVarElem.Provider.class);
        when(varProvider.getVar(eq("authored.author"))).thenReturn("hello");
        when(varProvider.getVar(eq("messaged.message"))).thenReturn("world");
        when(varProvider.requireVar(notNull())).thenCallRealMethod();

        Env env = Mockito.mock(Env.class);
        when(env.require(any())).thenCallRealMethod();
        when(env.get(eq(AnyVarElem.Provider.class))).thenReturn(varProvider);

        ElemParser parser = new ElemParser(Collections.singletonList(new AnyVarElem.Factory()));
        Substitutable elem = parser.parseElement("$authored.author >> $messaged.message");
        String value = elem.substitute(env);
        assertEquals("hello >> world", value);
    }

}