package live.dobbie.core.settings.upgrader;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.source.jackson.JacksonSource;
import live.dobbie.core.util.io.StringSupplier;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SchemaUpgraderTest {

    @Test
    void basicTest() throws ParserException, IOException {
        JacksonSource source = new JacksonSource(new StringSupplier(""));
        source.load();
        SchemaUpgrader upgrader = SchemaUpgrader.builder().register(new Upgrader(0) {
            @Override
            public void upgrade(@NonNull ISettingsSection section) throws ParserException {
                //section.getSection("schema").setValue(0);
            }
        }).build();
        assertTrue(upgrader.isNotCompatible(source));
        upgrader.tryUpgrade(source);
        assertFalse(upgrader.isNotCompatible(source));
        assertEquals(0, source.getRootSection().getSection("schema").getInteger());
        assertEquals(new SchemaVersion(0), source.getRootSection().getValue(SchemaVersion.class));
    }

    @Test
    void multipleTest() throws ParserException, IOException {
        JacksonSource source = new JacksonSource(new StringSupplier(""));
        source.load();
        SchemaUpgrader upgrader = SchemaUpgrader.builder()
                .register(new Upgrader(0) {
                    @Override
                    public void upgrade(@NonNull ISettingsSection section) throws ParserException {
                        section.getSection("schema").setValue(0);
                    }
                })
                .register(new Upgrader(1) {
                    @Override
                    public void upgrade(@NonNull ISettingsSection section) throws ParserException {
                        section.getSection("foo").setValue("bar");
                    }
                }).build();
        upgrader.tryUpgrade(source);
        assertEquals("bar", source.getRootSection().getSection("foo").getString());
        assertEquals(new SchemaVersion(1), source.getRootSection().getValue(SchemaVersion.class));
    }

    @Test
    void failingUpgradeTest() throws IOException, ParserException {
        JacksonSource source = new JacksonSource(new StringSupplier(""));
        source.load();
        SchemaUpgrader upgrader = SchemaUpgrader.builder()
                .register(new Upgrader(SchemaVersion.UNKNOWN.getVersion()) {
                    @Override
                    public void upgrade(@NonNull ISettingsSection section) throws ParserException {
                        section.getSection("schema").setValue(1);
                    }
                })
                .register(new Upgrader(2) {
                    @Override
                    public void upgrade(@NonNull ISettingsSection section) throws ParserException {
                        throw new SchemaUpgraderException("test");
                    }
                }).build();
        Assertions.assertThrows(SchemaUpgraderException.class, () -> upgrader.tryUpgrade(source));
    }
}