package live.dobbie.core.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import live.dobbie.core.misc.currency.Currency;
import live.dobbie.core.misc.currency.ICUDefaultCurrencyFormatter;
import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import live.dobbie.icu.util.ULocale;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@JacksonParseable("locale")
@Value
public class DobbieLocale implements ISettingsValue {
    /**
     * ICU Locale used to format numbers, texts, etc.
     */
    @JsonDeserialize(converter = ULocaleDeserializer.class)
    @NonNull ULocale locale;

    /**
     * This pattern may be used to format {@link Currency} that does not conform to ISO 4217.
     * If this is {@code null}, then {@link ICUDefaultCurrencyFormatter#DEFAULT_CURRENCY_PATTERN} is used.
     *
     * @see ICUDefaultCurrencyFormatter
     */
    String defaultCurrencyPattern;

    public static class ULocaleDeserializer extends StdConverter<String, ULocale> {
        @Override
        public ULocale convert(String value) {
            ULocale locale;
            if (value == null) {
                locale = ULocale.getDefault();
            } else {
                locale = ULocale.forLanguageTag(value);
            }
            return locale;
        }
    }
}
