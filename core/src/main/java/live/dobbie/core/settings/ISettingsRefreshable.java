package live.dobbie.core.settings;

import live.dobbie.core.util.Cleanable;

public interface ISettingsRefreshable extends Cleanable {
    boolean refreshValues();
}
