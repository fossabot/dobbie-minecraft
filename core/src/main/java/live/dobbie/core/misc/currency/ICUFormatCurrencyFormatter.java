package live.dobbie.core.misc.currency;

import live.dobbie.core.config.DobbieLocale;
import lombok.NonNull;

public class ICUFormatCurrencyFormatter extends ICUPlainCurrencyFormatter {
    private final @NonNull String pattern;

    public ICUFormatCurrencyFormatter(@NonNull String pattern, @NonNull String currency) {
        super(currency);
        this.pattern = pattern;
    }

    @Override
    protected String getPattern(@NonNull DobbieLocale locale) {
        return pattern;
    }
}
