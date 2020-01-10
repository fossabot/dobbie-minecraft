package live.dobbie.minecraft.bukkit;

import live.dobbie.core.util.logging.JavaLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BukkitLogger extends Logger {

    private final String prefix;

    protected BukkitLogger(Logger pluginLogger, String name) {
        super(name, null);
        this.prefix = "[" + name + "] ";
        this.setParent(pluginLogger);
        this.setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(this.prefix + record.getMessage());
        super.log(record);
    }

    @RequiredArgsConstructor
    public static class Factory extends JavaLogger.Factory {
        private final @NonNull Logger pluginLogger;

        @Override
        public JavaLogger getLogger(String name) {
            return new JavaLogger(new BukkitLogger(pluginLogger, name));
        }
    }
}
