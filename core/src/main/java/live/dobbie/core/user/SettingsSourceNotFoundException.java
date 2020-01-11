package live.dobbie.core.user;

public class SettingsSourceNotFoundException extends RuntimeException {
    public SettingsSourceNotFoundException(String message) {
        super(message);
    }
}
