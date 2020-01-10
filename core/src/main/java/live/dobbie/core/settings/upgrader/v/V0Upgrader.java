package live.dobbie.core.settings.upgrader.v;

import live.dobbie.core.config.DobbieConfig;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.settings.object.section.ISettingsSection;
import live.dobbie.core.settings.upgrader.Upgrader;
import lombok.NonNull;

public class V0Upgrader extends Upgrader {

    private final String brand;
    private final String helpMessage;

    public V0Upgrader(@NonNull String brand, @NonNull String helpMessage) {
        super(0);
        this.brand = brand;
        this.helpMessage = helpMessage;
    }

    @Override
    public void upgrade(@NonNull ISettingsSection section) throws ParserException {
        section.getSection("brand").setValue(brand);
        section.getSection("_help").setValue(helpMessage);
        section.getSection("timer", "ticks", "timeoutBetween").setValue(DobbieConfig.Timer.Ticks.DEFAULT.getTimeoutBetween());
        section.getSection("timer", "ticks", "reloadEvery").setValue(DobbieConfig.Timer.Ticks.DEFAULT.getReloadEvery());
    }
}
