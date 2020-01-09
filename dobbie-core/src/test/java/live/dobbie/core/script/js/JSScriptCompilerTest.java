package live.dobbie.core.script.js;

import live.dobbie.core.script.ScriptCompilationException;
import live.dobbie.core.script.ScriptSource;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.ContextFactory;

import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class JSScriptCompilerTest {

    @Test
    void cacheTest() throws ScriptCompilationException, IOException {
        ContextFactory cxf = new ContextFactory();
        JSScriptCompiler compiler = new JSScriptCompiler(cxf);
        Supplier<ScriptSource> source = () -> ScriptSource.fromString("'hello'", "test");

        JSScript
                script0 = compiler.compile(source.get()),
                script1 = compiler.compile(source.get());
        assertNotNull(script0);
        assertNotNull(script1);
        assertSame(script0, script1);
    }

}