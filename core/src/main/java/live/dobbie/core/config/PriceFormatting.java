package live.dobbie.core.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import live.dobbie.core.misc.Price;
import live.dobbie.core.misc.currency.ICUPlainCurrencyFormatter;
import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@JsonDeserialize(converter = PriceFormatting.Deserializer.class)
@JacksonParseable("priceFormatting")
@Value
public class PriceFormatting implements ISettingsValue {
    @NonNull Map<String, String> map;

    public String format(@NonNull Price price, @NonNull DobbieLocale locale) {
        String format = map.get(price.getCurrency().getName());
        return format == null ? null : ICUPlainCurrencyFormatter.format(format, price.getAmount(),
                price.getCurrency().getName(), locale);
    }

    public static String format(@NonNull Price price, @NonNull DobbieLocale locale, PriceFormatting priceFormatting) {
        if (priceFormatting != null) {
            String formatted = priceFormatting.format(price, locale);
            if (formatted != null) {
                return formatted;
            }
        }
        return price.format(locale);
    }

    public static class Deserializer extends StdConverter<Map<String, String>, PriceFormatting> {
        @Override
        public PriceFormatting convert(Map<String, String> stringStringMap) {
            Map<String, String> map = new HashMap<>();
            stringStringMap.forEach((k, v) -> map.put(k.toUpperCase(), v));
            return new PriceFormatting(map);
        }
    }
}
