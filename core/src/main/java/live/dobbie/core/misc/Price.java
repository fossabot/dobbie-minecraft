package live.dobbie.core.misc;

import live.dobbie.core.config.DobbieLocale;
import live.dobbie.core.misc.currency.Currency;
import lombok.NonNull;
import lombok.Value;

@Value
public class Price {
    @NonNull Number amount;
    @NonNull Currency currency;

    @NonNull
    public String format(@NonNull DobbieLocale locale) {
        return currency.format(amount, locale);
    }

    public static Price of(@NonNull Number amount, @NonNull String currency) {
        return new Price(amount, Currency.of(currency));
    }
}
