package live.dobbie.core.misc;

import live.dobbie.core.context.primitive.converter.ConvertableToString;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@ConvertableToString
public class Price {
    BigDecimal amount;
    @NonNull Currency currency;

    @Override
    public String toString() {
        return currency.getValue() + amount;
    }
}
