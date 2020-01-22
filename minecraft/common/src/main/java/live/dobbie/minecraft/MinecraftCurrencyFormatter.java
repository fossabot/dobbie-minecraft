package live.dobbie.minecraft;

import live.dobbie.core.config.DobbieLocale;
import live.dobbie.core.misc.currency.CurrencyFormatter;
import live.dobbie.core.misc.currency.ICUCurrencyFormatter;
import live.dobbie.icu.util.Currency;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;

@RequiredArgsConstructor
public class MinecraftCurrencyFormatter implements CurrencyFormatter {
    private final @NonNull CurrencyFormatter delegate;

    @Override
    public @NonNull String format(@NonNull Number amount, @NonNull DobbieLocale locale) {
        String result = delegate.format(amount, locale);
        result = StringUtils.replaceChars(result, "\u00A0", " "); // Minecraft does not render NBSP
        return result;
    }

    @RequiredArgsConstructor
    public static class Factory implements ICUCurrencyFormatter.Factory {
        private final @NonNull ICUCurrencyFormatter.Factory delegateFactory;

        @Override
        public CurrencyFormatter create(@NonNull Currency currency) {
            return new MinecraftCurrencyFormatter(delegateFactory.create(currency));
        }
    }
}
