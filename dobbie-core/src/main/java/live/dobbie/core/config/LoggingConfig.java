package live.dobbie.core.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class LoggingConfig {
    User toUser;
    Console toConsole;

    @Value
    public static class User {
        boolean enabled;
    }

    @Value
    public static class Console {
        boolean enabled;
    }
}
