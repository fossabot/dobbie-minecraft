package live.dobbie.core.substitutor.old;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.old.func.FuncElem;
import live.dobbie.core.substitutor.old.func.NameFuncElem;
import live.dobbie.core.substitutor.old.func.NoNameFuncElem;
import live.dobbie.core.substitutor.old.parser.ElemParser;
import live.dobbie.core.substitutor.old.var.VarElem;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElemTest {

    @Test
    void noFactoriesTest() throws ParserException {
        ElemParser parser = new ElemParser(Collections.emptyList());
        Substitutable elem = parser.parseElement("hello, world!");
        Env env = Mockito.mock(Env.class);
        assertEquals("hello, world!", elem.substitute(env));
    }

    @Test
    void varTest() throws ParserException {
        ElemParser parser = new ElemParser(Collections.singletonList(new VarElem.Factory("foo", env -> "bar")));
        Substitutable elem = parser.parseElement("hello, $foo!");
        Env env = Mockito.mock(Env.class);
        assertEquals("hello, bar!", elem.substitute(env));
    }

    @Test
    void funcTest() throws ParserException {
        ElemParser parser = new ElemParser(Collections.singletonList(new NameFuncElem.Factory("concat",
                (FuncElem.ArgFunc) (env, argument) -> argument, 1, Integer.MAX_VALUE)
        ));
        Substitutable elem = parser.parseElement("hello, $concat{w,o,r,l,d}!");
        Env env = Mockito.mock(Env.class);
        assertEquals("hello, world!", elem.substitute(env));
    }

    @Test
    void noNameFuncTest() throws ParserException {
        ElemParser parser = new ElemParser(Collections.singletonList(new NoNameFuncElem.Factory(
                (env, arguments) -> StringUtils.join(arguments, ":"), 1, Integer.MAX_VALUE
        )));
        Substitutable elem = parser.parseElement("${1,2,3}");
        Env env = Mockito.mock(Env.class);
        assertEquals("1:2:3", elem.substitute(env));
    }

    @Test
    void complexTest() throws ParserException {
        ElemParser parser = new ElemParser(Arrays.asList(
                new VarElem.Factory("a", env -> "A"),
                new VarElem.Factory("b", env -> "B"),
                new NameFuncElem.Factory("a", (env, arguments) ->
                        arguments.stream().map(String::toLowerCase).collect(Collectors.joining()), 1, Integer.MAX_VALUE)
        )
        );
        Substitutable elem = parser.parseElement("$a{$a,$b,C}");
        Env env = Mockito.mock(Env.class);
        assertEquals("abc", elem.substitute(env));
    }

    @Test
    void missingBracketTest() {
        ElemParser parser = new ElemParser(Collections.singletonList(new NameFuncElem.Factory("concat",
                (FuncElem.ArgFunc) (env, argument) -> argument, 1, Integer.MAX_VALUE)
        ));
        Assertions.assertThrows(ParserException.class, () -> parser.parseElement("$concat{foo!"));
    }

    @Test
    void duplicateBracketTest() {
        ElemParser parser = new ElemParser(Collections.singletonList(new NameFuncElem.Factory("concat",
                (FuncElem.ArgFunc) (env, argument) -> argument, 1, Integer.MAX_VALUE)
        ));
        Assertions.assertThrows(ParserException.class, () -> parser.parseElement("$concat{{foo!}"));
    }

    @Test
    void varSpaceTest() throws ParserException {
        ElemParser parser = new ElemParser(Arrays.asList(
                new VarElem.Factory("authored.author", env -> "hello"),
                new VarElem.Factory("messaged.message", env -> "world")
        ));
        Substitutable elem = parser.parseElement("$authored.author >> $messaged.message");
        Env env = Mockito.mock(Env.class);
        assertEquals("hello >> world", elem.substitute(env));
    }

    @Test
    void varCharsSpaceTest() throws ParserException {
        ElemParser parser = new ElemParser(Collections.singletonList(
                new VarElem.Factory("hello", env -> "world")
        ));
        Substitutable elem = parser.parseElement("($hello)");
        Env env = Mockito.mock(Env.class);
        assertEquals("(world)", elem.substitute(env));
    }
}