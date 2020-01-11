package live.dobbie.core.settings.source.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.dobbie.core.settings.source.supplier.SettingsIOSourceFactory;
import live.dobbie.core.settings.upgrader.SchemaUpgrader;
import live.dobbie.core.user.SettingsSourceProvider;
import live.dobbie.core.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JacksonSourceProvider implements SettingsSourceProvider<JacksonNode> {
    private final @NonNull ObjectMapper objectMapper;
    private final SchemaUpgrader upgrader;
    private final @NonNull SettingsIOSourceFactory userIOSupplier;

    @Override
    public JacksonSource getSettings(@NonNull User user) {
        return new JacksonSource(objectMapper, userIOSupplier.getSettingsIOSupplier(user), upgrader);
    }

}
