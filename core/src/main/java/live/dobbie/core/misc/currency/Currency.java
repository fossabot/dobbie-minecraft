package live.dobbie.core.misc.currency;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.*;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "name")
public class Currency implements CurrencyFormatter {
    private final @NonNull
    @Getter
    String name;
    private final @NonNull
    @Delegate
    CurrencyFormatter formatter;

    @SneakyThrows
    public static Currency of(@NonNull String currencyName) {
        return cache.get(convertCurrencyName(currencyName));
    }

    public static Currency register(@NonNull String currencyName, @NonNull CurrencyFormatter formatter) {
        currencyName = convertCurrencyName(currencyName);
        Currency currency = cache.getIfPresent(currencyName);
        if (currency != null) {
            throw new RuntimeException("currency \"" + currencyName + "\" already registered");
        }
        currency = new Currency(currencyName, formatter);
        cache.put(currencyName, currency);
        return currency;
    }

    private static String convertCurrencyName(String currencyName) {
        return currencyName.toUpperCase();
    }

    private static Currency createFrom(@NonNull String currencyName) {
        com.ibm.icu.util.Currency icuCurrency = ICUCurrencyCache.get(currencyName);
        CurrencyFormatter underlyingFormatter;
        if (icuCurrency == null) {
            underlyingFormatter = new ICUDefaultCurrencyFormatter(currencyName);
        } else {
            underlyingFormatter = new ICUCurrencyFormatter(icuCurrency);
        }
        return new Currency(currencyName, underlyingFormatter);
    }

    private static final LoadingCache<String, Currency> cache = CacheBuilder.newBuilder().softValues()
            .build(new CacheLoader<String, Currency>() {
                @Override
                public Currency load(@NotNull String currencyName) {
                    return createFrom(currencyName);
                }
            });
}
