package live.dobbie.core.misc.currency;

import live.dobbie.core.config.DobbieLocale;
import live.dobbie.icu.text.NumberFormat;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
public class ICUCurrencyFormatter implements CurrencyFormatter {
    @NonNull live.dobbie.icu.util.Currency icuCurrency;

    @NotNull
    @Override
    public String format(@NonNull Number amount, @NonNull DobbieLocale locale) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale.getLocale());
        format.setCurrency(icuCurrency);
        return format.format(amount);
    }
}
