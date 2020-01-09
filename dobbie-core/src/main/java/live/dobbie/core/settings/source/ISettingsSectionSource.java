package live.dobbie.core.settings.source;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.section.ISettingsSection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

public interface ISettingsSectionSource<O extends ISettingsSection> extends ISettingsSource<O> {
    @Override
    default @NonNull O getObject() {
        return getRootSection();
    }

    @NonNull O getRootSection();

    @RequiredArgsConstructor
    class Delegated<S extends ISettingsSectionSource<O>, O extends ISettingsSection> implements ISettingsSectionSource<O> {
        private final @NonNull
        @Getter(AccessLevel.PROTECTED)
        S source;

        @Override
        @NonNull
        public O getObject() {
            return source.getObject();
        }

        @Override
        @NonNull
        public O getRootSection() {
            return source.getRootSection();
        }

        @Override
        public void load() throws IOException, ParserException {
            source.load();
        }

        @Override
        public void save() throws IOException, ParserException {
            source.save();
        }
    }
}
