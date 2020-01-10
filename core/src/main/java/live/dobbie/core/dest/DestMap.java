package live.dobbie.core.dest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import live.dobbie.core.path.Path;
import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode
@JacksonParseable("destinations")
@JsonDeserialize(converter = DestMap.Deserializer.class)
public class DestMap implements ISettingsValue, DestSectionLocator {
    private static final ILogger LOGGER = Logging.getLogger(DestMap.class);

    private final @NonNull Map<String, Dest> destByName;


    public Dest get(@NonNull String name) {
        return destByName.get(name);
    }

    @NonNull
    public Dest require(@NonNull String name) {
        Dest dest = get(name);
        if (dest == null) {
            throw new IllegalArgumentException("destination not found: \"" + name + "\"");
        }
        return dest;
    }


    @Override
    public DestSection getSection(@NonNull Path path) {
        try {
            return requireSection(path);
        } catch (IllegalArgumentException e) {
            LOGGER.tracing("Could not find section by path: " + Path.toString(path), e);
            return null;
        }
    }

    @NonNull
    @Override
    public DestSection requireSection(@NonNull Path path) throws IllegalArgumentException {
        try {
            path.ensureSize(2);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("invalid section path \"" + Path.toString(path) + "\"; expected exactly 2 elements");
        }
        String
                destName = path.at(0),
                sectionName = path.at(1);
        try {
            Dest dest = require(destName);
            return dest.requireSection(sectionName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("could not find section \"" + Path.toString(path) + "\"", e);
        }
    }

    public static class Deserializer extends StdConverter<List<Dest>, DestMap> {
        @Override
        @NonNull
        public DestMap convert(List<Dest> value) {
            return new DestMap(extractNamesIntoMap(value));
        }

        @NonNull
        private static Map<String, Dest> extractNamesIntoMap(List<Dest> list) {
            return list.stream().collect(
                    Collectors.toMap(Dest::getName, Function.identity())
            );
        }
    }
}
