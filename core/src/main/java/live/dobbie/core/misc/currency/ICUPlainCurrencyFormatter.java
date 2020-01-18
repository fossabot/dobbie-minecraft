package live.dobbie.core.misc.currency;

import com.ibm.icu.text.MessageFormat;
import live.dobbie.core.config.DobbieLocale;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.text.FieldPosition;
import java.util.HashMap;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class ICUPlainCurrencyFormatter implements CurrencyFormatter {
    @NonNull String currency;

    @Override
    public final @NonNull String format(@NonNull Number amount, @NonNull DobbieLocale locale) {
        return format(getPattern(locale), amount, currency, locale);
    }

    public static @NonNull String format(@NonNull String pattern,
                                         @NonNull Number amount,
                                         @NonNull String currency,
                                         @NonNull DobbieLocale locale) {
        MessageFormat mf = new MessageFormat(pattern, locale.getLocale());
        Map<String, Object> args = new HashMap<>();
        args.put("amount", amount);
        args.put("currency", currency);
        StringBuffer b = new StringBuffer();
        mf.format(args, b, new FieldPosition(0));
        return b.toString();
    }

    protected abstract String getPattern(@NonNull DobbieLocale locale);
}
