package live.dobbie.core.misc;

import live.dobbie.core.context.primitive.converter.ConvertableToString;
import lombok.NonNull;
import lombok.Value;

@Value
@ConvertableToString
public class Price {
    double amount;
    @NonNull Currency currency;

    @Override
    public String toString() {
        return currency.getValue() + amount;
    }
}
