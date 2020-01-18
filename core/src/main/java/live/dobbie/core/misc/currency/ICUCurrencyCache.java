package live.dobbie.core.misc.currency;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ibm.icu.util.Currency;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Optional;

public class ICUCurrencyCache {
    private static final LoadingCache<String, Optional<com.ibm.icu.util.Currency>> currencyCache = CacheBuilder.newBuilder().softValues()
            .build(new CacheLoader<String, Optional<com.ibm.icu.util.Currency>>() {
                @Override
                public Optional<com.ibm.icu.util.Currency> load(@NonNull String currency) throws Exception {
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
