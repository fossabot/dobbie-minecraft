package live.dobbie.core.misc.currency;

import live.dobbie.core.config.DobbieLocale;
import lombok.NonNull;

public class ICUDefaultCurrencyFormatter extends ICUPlainCurrencyFormatter {
    public static final String DEFAULT_CURRENCY_PATTERN = "{amount} {currency}";

    public ICUDefaultCurrencyFormatter(@NonNull String currency) {
        super(currency);
    }

    @Override
    protected String getPattern(@NonNull DobbieLocale locale) {
        return getDefaultCurrencyPattern(locale);
    }

    private static String getDefaultCurrencyPattern(DobbieLocale locale) {
        String defaultCurrencyPattern = locale.getDefaultCurrencyPattern();
        return defaultCurrencyPattern == null ? DEFAULT_CURRENCY_PATTERN : defaultCurrencyPattern;
    }
}
