package live.dobbie.core.misc.currency;

import live.dobbie.core.config.DobbieLocale;
import live.dobbie.icu.text.NumberFormat;
import live.dobbie.icu.util.Currency;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ICUCurrencyFormatter implements CurrencyFormatter {
    protected final @NonNull Currency icuCurrency;

    public interface Factory {
        CurrencyFormatter create(@NonNull Currency currency);
    }

    private static Factory FACTORY;

    public static Factory getFactory() {
        if (FACTORY == null) {
            FACTORY = Default.Factory.INSTANCE;
        }
        return FACTORY;
    }

    public static void setFactory(@NonNull Factory factory) {
        FACTORY = factory;
    }

    public static class Default extends ICUCurrencyFormatter {
        public Default(@NonNull Currency icuCurrency) {
            super(icuCurrency);
        }

        @Override
        public @NonNull String format(@NonNull Number amount, @NonNull DobbieLocale locale) {
            NumberFormat format = NumberFormat.getCurrencyInstance(locale.getLocale());
            format.setCurrency(icuCurrency);
            return format.format(amount);
        }

        public static class Factory implements ICUCurrencyFormatter.Factory {
            public static Factory INSTANCE = new Factory();

            @Override
            public CurrencyFormatter create(@NonNull Currency currency) {
                return new Default(currency);
            }
        }
    }
}
