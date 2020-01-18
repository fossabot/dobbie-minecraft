package live.dobbie.core.loc;

import live.dobbie.core.misc.Price;
import lombok.NonNull;

import java.util.Map;

public abstract class LocString {
    @NonNull
    public abstract LocString set(@NonNull String arg, String value);

    @NonNull
    public abstract LocString set(@NonNull String arg, Number number);

    @NonNull
    public abstract LocString set(@NonNull String arg, Price price);

    @NonNull
    public abstract LocString set(@NonNull String arg, LocString nestedLocString);

    @NonNull
    public abstract LocString set(@NonNull String arg, ToLocString nestedToLocString);

    @NonNull
    public abstract LocString set(@NonNull String arg, @NonNull Subject subject);

    @NonNull
    public abstract LocString copy(LocString storage);

    public abstract LocString key(@NonNull String key);

    @NonNull
    public abstract String build();

    abstract Map<String, Object> values();
}
