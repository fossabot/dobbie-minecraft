package live.dobbie.core.settings.upgrader;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.source.ISettingsSectionSource;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SchemaUpgrader {
    private final @NonNull Map<Integer, Upgrader> upgraders;
    private final int latestVersion;

    public boolean isNotCompatible(@NonNull ISettingsSectionSource source) {
        try {
            return getSchemaVersion(source) != latestVersion;
        } catch (ParserException exception) {
            return true;
        }
    }

    public void tryUpgrade(@NonNull ISettingsSectionSource source) throws ParserException, IOException {
        int schemaVersion;
        ParserException suppressed = null;
        try {
            schemaVersion = getSchemaVersion(source);
        } catch (ParserException parserException) {
            if (!source.getRootSection().isEmpty()) {
                throw new SchemaUpgraderException("existing unknown schema is not empty. make the backup!", parserException);
            }
            schemaVersion = SchemaVersion.UNKNOWN.getVersion();
            suppressed = parserException;
        }
        if (schemaVersion == latestVersion) {
            throw reportError("schema is already the latest version (" + schemaVersion + ")", suppressed);
        }
        if (schemaVersion > latestVersion) {
            throw reportError("schema was created in a newer version, cannot downgrade (got: " + schemaVersion + ", latest supported by this version:" + latestVersion + ")", suppressed);
        }
        ISettingsSection rootSection = source.getObject();
        while (schemaVersion < latestVersion) {
            Upgrader upgrader = this.upgraders.get(schemaVersion);
            if (upgrader == null) {
                throw reportError("could not find upgrader for version " + schemaVersion, suppressed);
            }
            upgrader.upgrade(rootSection);
            schemaVersion = upgrader.getToVersion();
            rootSection.getSection("schema").setValue(schemaVersion);
            source.save();
        }
    }

    private static SchemaUpgraderException reportError(String message, Throwable suppressed) {
        SchemaUpgraderException e = new SchemaUpgraderException(message);
        e.addSuppressed(suppressed);
        return e;
    }

    private static int getSchemaVersion(ISettingsSectionSource source) throws ParserException {
        SchemaVersion schema = source.getObject().getValue(SchemaVersion.class);
        if (schema == null) {
            throw new ParserException("schema version is missing");
        }
        return schema.getVersion();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Integer, Upgrader> upgraders = new HashMap<>();
        private int latestVersion;

        Builder() {
        }

        public Builder register(@NonNull Upgrader upgrader) {
            this.latestVersion = Math.max(upgrader.getToVersion(), latestVersion);
            Integer fromVersion = upgrader.getFromVersion();
            if (upgraders.containsKey(fromVersion)) {
                throw new IllegalArgumentException("upgrader from version " + fromVersion + " already registered");
            }
            upgraders.put(fromVersion, upgrader);
            return this;
        }

        public SchemaUpgrader build() {
            return new SchemaUpgrader(upgraders, latestVersion);
        }
    }
}
