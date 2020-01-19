package live.dobbie.core.misc.currency;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import live.dobbie.icu.util.Currency;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Optional;

public class ICUCurrencyCache {
    private static final LoadingCache<String, Optional<Currency>> currencyCache = CacheBuilder.newBuilder().softValues()
            .build(new CacheLoader<String, Optional<Currency>>() {
                @Override
                public Optional<Currency> load(@NonNull String currency) throws Exception {
                    return Currency.getAvailableCurrencies()
                            .stream()
                            .filter(c -> c.getCurrencyCode().equals(currency))
                            .findAny();
                }
            });

    @SneakyThrows
    public static Currency get(@NonNull String currency) {
        return currencyCache.get(currency).orElse(null);
    }

    private ICUCurrencyCache() {
    }
}
