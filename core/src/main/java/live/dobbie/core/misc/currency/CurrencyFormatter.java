package live.dobbie.core.misc.currency;

import live.dobbie.core.config.DobbieLocale;
import lombok.NonNull;

public interface CurrencyFormatter {
    @NonNull String format(@NonNull Number amount, @NonNull DobbieLocale locale);
}
