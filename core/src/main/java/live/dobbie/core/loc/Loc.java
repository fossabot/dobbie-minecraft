package live.dobbie.core.loc;

import live.dobbie.core.config.DobbieLocale;
import live.dobbie.core.misc.Price;
import live.dobbie.icu.text.MessageFormat;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class Loc {
    private static final InternalLocSource INTERNAL = new InternalLocSource();

    private static final String
            GENDER_SUFFIX = "_gender",
            AMOUNT_SUFFIX = "_amount",
            CURRENCY_SUFFIX = "_currency";

    private final @NonNull LocSource source;
    private @Getter
    @Setter
    DobbieLocale locale;

    public Loc() {
        this(INTERNAL, DobbieLocale.BY_DEFAULT);
    }

    @NonNull
    public LocString args() {
        return new Lightweight();
    }

    @NonNull
    public LocString withKey(@NonNull String key) {
        String translatedKey = source.getTranslation(key);
        if (translatedKey == null) {
            translatedKey = INTERNAL.getTranslation(key);
        }
        return new Complete(translatedKey);
    }

    private class Lightweight extends LocString {
        private final Map<String, Object> values = new HashMap<>(2, 1.f);

        @Override
        Map<String, Object> values() {
            return values;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, String value) {
            put(arg, value);
            return this;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, Number number) {
            put(arg, number);
            return this;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, Price price) {
            put(arg, price);
            put(arg + AMOUNT_SUFFIX, price.getAmount());
            put(arg + CURRENCY_SUFFIX, price.getCurrency().getName());
            return this;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, LocString nestedLocString) {
            put(arg, nestedLocString);
            return this;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, ToLocString nestedToLocString) {
            return set(arg, nestedToLocString == null ? null : nestedToLocString.toLocString(Loc.this));
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, @NonNull Subject subject) {
            put(arg, subject.getName());
            put(arg + GENDER_SUFFIX, subject.getGender());
            return this;
        }

        @Override
        public @NonNull LocString copy(LocString storage) {
            values.putAll(storage.values());
            return this;
        }

        @Override
        public LocString key(@NonNull String key) {
            return Loc.this.withKey(key).copy(this);
        }

        private void put(String arg, Object value) {
            values.put(arg, value);
            values.put(arg + "_present", value == null ? "none" : "present");
        }

        @Override
        public @NonNull String build() {
            throw new IllegalStateException("build() must not be called on lightweight LocString");
        }
    }

    @RequiredArgsConstructor
    private class Complete extends Lightweight {
        private final @NonNull String format;

        @Override
        public LocString key(@NonNull String key) {
            throw new IllegalStateException("key already set on Complete LocString");
        }

        @Override
        public @NonNull String build() {
            MessageFormat mf = new MessageFormat(format, locale.getLocale());
            Map<String, Object> icuFormatValues = new HashMap<>();
            values().forEach((key, value) -> icuFormatValues.put(key, toICUValue(value)));
            StringBuffer b = new StringBuffer();
            mf.format(icuFormatValues, b, null);
            return b.toString();
        }
    }

    private Object toICUValue(Object rawValue) {
        if (rawValue instanceof Price) {
            return ((Price) rawValue).format(locale);
        }
        if (rawValue instanceof Gender) {
            return ((Gender) rawValue).formatValue();
        }
        if (rawValue instanceof LocString) {
            LocString c = (LocString) rawValue;
            return c.build();
        }
        return rawValue;
    }

    private static class InternalLocSource implements LocSource {
        @Override
        @NonNull
        public String getTranslation(@NonNull String key) {
            return key;
        }
    }
}
