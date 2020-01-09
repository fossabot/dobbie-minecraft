package live.dobbie.core.settings.source.validatable;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.path.Path;
import live.dobbie.core.settings.object.ISettingsObject;
import live.dobbie.core.settings.object.section.ISettingsSection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

public interface SettingsSourceValidator<O extends ISettingsObject> {
    void checkValid(@NonNull O object) throws ParserException;

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class ByString implements SettingsSourceValidator<ISettingsSection> {
        @NonNull Path path;
        @NonNull String expectedValue;

        @Override
        public void checkValid(@NonNull ISettingsSection s) throws ParserException {
            ISettingsSection section = s.getSection(path);
            String actualValue = section.requireExist().getString();
            if (!expectedValue.equals(actualValue)) {
                throw new ParserException(Path.toString(path) + " is expected to be \"" + expectedValue + "\", but got \"" + actualValue + "\" instead");
            }
        }
    }
}
