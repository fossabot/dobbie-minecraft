package live.dobbie.core.settings.upgrader;

import live.dobbie.core.exception.ParserException;

public class SchemaUpgraderException extends ParserException {
    public SchemaUpgraderException(String message) {
        super(message);
    }

    public SchemaUpgraderException(String message, Throwable cause) {
        super(message, cause);
    }
}
