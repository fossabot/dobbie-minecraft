package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.upgrader.SchemaUpgrader;
import live.dobbie.core.settings.upgrader.SchemaUpgraderException;
import live.dobbie.core.settings.upgrader.SchemaVersion;
import live.dobbie.core.settings.upgrader.Upgrader;
import live.dobbie.core.util.io.IOSupplier;
import live.dobbie.core.util.io.StringSupplier;
import live.dobbie.core.util.io.mod.ModSignal;
import live.dobbie.core.util.io.mod.ObjectModSignal;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JacksonSourceTest {

    @Test
    void basicTest() throws IOException, ParserException {
        JacksonSource source = new JacksonSource(new ObjectMapper(), new StringSupplier("{\"foo\": \"bar\"}"));
        source.load();
        assertEquals("bar", source.getObject().getSection("foo").getValue());
    }

    @Test
    void basicYamlTest() throws IOException, ParserException {
        JacksonSource source = new JacksonSource(new ObjectMapper(new YAMLFactory()), new StringSupplier("foo: bar"));
        source.load();
        assertEquals("bar", source.getObject().getSection("foo").getValue());
    }

    @Test
    void emptyFileTest() {
        JacksonSource source = new JacksonSource(new ObjectMapper(), new StringSupplier(""));
        assertDoesNotThrow(source::load);
    }

    @Test
    void invalidFileTest() {
        JacksonSource source = new JacksonSource(new ObjectMapper(), new StringSupplier("[]"));
        Assertions.assertThrows(ParserException.class, source::load);
    }

    @Test
    void basicSaveTest() throws IOException, ParserException {
        StringSupplier str = new StringSupplier("{}");
        JacksonSource source = new JacksonSource(new ObjectMapper(), str);
        source.load();
        source.getObject().getSection("foo").setValue("bar");
        source.save();
        assertEquals("{\"foo\":\"bar\"}", str.getStr());
    }

    @Test
    void dontSaveIfNotModifiedTest() throws IOException, ParserException {
        StringSupplier str = Mockito.spy(new StringSupplier("{}"));
        JacksonSource source = new JacksonSource(new ObjectMapper(), str);
        source.load();
        source.save();
        verify(str, never()).output();
    }

    @Test
    void ioExceptionTest() throws IOException {
        IOSupplier io = Mockito.mock(IOSupplier.class);
        when(io.getModSignal()).thenAnswer((Answer<ModSignal>) invocation -> new ObjectModSignal(new Object()));
        when(io.input()).thenThrow(new IOException());
        when(io.output()).thenThrow(new IOException());

        // load
        Assertions.assertThrows(IOException.class, () -> new JacksonSource(new ObjectMapper(), io).load(), "failing load did not throw any exception");

        // save
        JacksonSource source = new JacksonSource(io);
        source.getContext().modIncrement();
        assertThrows(IOException.class, source::save, "failing save did not throw any exception");
    }

    @Test
    void emptyOnFailedUpgradeTest() throws ParserException {
        SchemaUpgrader upgrader = SchemaUpgrader.builder()
                .register(new Upgrader(SchemaVersion.UNKNOWN.getVersion()) {
                    @Override
                    public void upgrade(@NonNull ISettingsSection section) throws ParserException {
                        section.getSection("schema").setValue(1);
                    }
                })
                .register(new Upgrader(1) {
                    @Override
                    public void upgrade(@NonNull ISettingsSection section) throws ParserException {
                        throw new SchemaUpgraderException("test");
                    }
                }).build();
        JacksonSource source = new JacksonSource(new StringSupplier("{\"foo\":\"bar\"}"), upgrader);
        Assertions.assertThrows(ParserException.class, source::load);
        assertTrue(source.getRootSection().isEmpty());
    }

    @Test
    void modSignalTest() throws ParserException, IOException {
        StringSupplier supplier = Mockito.spy(new StringSupplier("{}"));
        JacksonSource source = new JacksonSource(supplier);
        source.load();
        verify(supplier, times(1)).input();
        source.load();
        verify(supplier, times(1)).input();
        when(supplier.getModSignal()).thenReturn(new ObjectModSignal(new Object()));
        source.load();
        verify(supplier, times(2)).input();
    }
}