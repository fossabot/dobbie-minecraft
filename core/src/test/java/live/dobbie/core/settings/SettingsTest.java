package live.dobbie.core.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.listener.ISettingsListener;
import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.parser.ISettingsParser;
import live.dobbie.core.settings.source.jackson.JacksonSource;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.core.util.io.IOSupplier;
import live.dobbie.core.util.io.StringSupplier;
import live.dobbie.core.util.io.mod.UnknownModSignal;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsTest {

    @Test
    void basicTest() throws IOException, ParserException {
        JacksonSource source = new JacksonSource(new ObjectMapper(), new StringSupplier("{\"foo\": \"bar\"}"));
        source.load();
        ISettingsParser.Provider provider = Mockito.mock(ISettingsParser.Provider.class);
        when(provider.findParser(Foo.class)).thenReturn((ISettingsParser<ISettingsSection, Foo>) (source1, context) -> new Foo(source1.getSection("foo").getString()));
        Settings settings = new Settings(source, provider);
        Foo foo = settings.getValue(Foo.class);
        assertNotNull(foo);
        assertEquals("bar", foo.foo);
    }

    @Test
    void listenerTest() throws IOException, ParserException {
        StringSupplier
                originalValue = new StringSupplier("{}"),
                newValue = new StringSupplier("{\"foo\": \"new\"}");
        IOSupplier supplier = Mockito.mock(IOSupplier.class);
        when(supplier.input()).then((Answer<InputStream>) invocation -> originalValue.input());
        when(supplier.getModSignal()).thenAnswer((Answer<UnknownModSignal>) invocation -> new UnknownModSignal());
        JacksonSource source = new JacksonSource(new ObjectMapper(), supplier);
        ISettingsParser.Provider provider = Mockito.mock(ISettingsParser.Provider.class);
        when(provider.findParser(Foo.class)).thenReturn((ISettingsParser<ISettingsSection, Foo>) (source1, context) -> new Foo(source1.getSection("foo").getString()));
        Settings settings = new Settings(source, provider);
        ISettingsListener<Foo> listener = Mockito.mock(ISettingsListener.class);
        assertFalse(settings.refreshValues());
        settings.registerListener(Foo.class, listener, false);
        verify(listener, times(0)).onSettingsChanged(any());
        when(supplier.input()).then((Answer<InputStream>) invocation -> newValue.input());
        doAnswer(invocation -> {
            assertEquals(new Foo("new"), invocation.getArgument(0));
            return null;
        }).when(listener).onSettingsChanged(any());
        assertTrue(settings.refreshValues());
        verify(listener, times(1)).onSettingsChanged(any());
    }

    @Value
    static class Foo implements ISettingsValue {
        String foo;
    }

}