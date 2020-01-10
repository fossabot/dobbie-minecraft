package live.dobbie.core.settings.upgrader;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.section.ISettingsSection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode(of = "fromVersion")
@ToString
public abstract class Upgrader {
    @Getter
    private final int fromVersion, toVersion;

    public Upgrader(int toVersion) {
        this.fromVersion = toVersion - 1;
        this.toVersion = toVersion;
    }

    public abstract void upgrade(@NonNull ISettingsSection section) throws ParserException;
}
