package live.dobbie.core.settings.source.validatable;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.source.ISettingsSectionSource;
import lombok.NonNull;

import java.io.IOException;

public class ValidatableSource<S extends ISettingsSectionSource<O>, O extends ISettingsSection> extends ISettingsSectionSource.Delegated<S, O> {
    private final @NonNull O emptySection;
    private final SettingsSourceValidator<O> validator;

    private boolean failedToValidate;

    public ValidatableSource(@NonNull S source, @NonNull O emptySection, @NonNull SettingsSourceValidator<O> validator) {
        super(source);
        this.emptySection = emptySection;
        this.validator = validator;
    }

    @Override
    public @NonNull O getObject() {
        if (failedToValidate) {
            return emptySection;
        }
        return super.getObject();
    }

    @Override
    public @NonNull O getRootSection() {
        if (failedToValidate) {
            return emptySection;
        }
        return super.getRootSection();
    }

    @Override
    public void load() throws IOException, ParserException {
        super.load();
        validate();
    }

    @Override
    public void save() throws IOException, ParserException {
        validate();
        super.save();
    }

    private void validate() throws ParserException {
        try {
            validator.checkValid(super.getObject());
        } catch (ParserException e) {
            failedToValidate = true;
            throw new ParserException("failed to validate source " + getSource(), e);
        }
        failedToValidate = false;
    }
}
