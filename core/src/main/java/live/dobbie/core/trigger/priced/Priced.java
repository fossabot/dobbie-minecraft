package live.dobbie.core.trigger.priced;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.misc.Price;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;

@ContextClass
public interface Priced extends Trigger {
    @NonNull Price getPrice();

    @ContextVar(path = {"price", "amount"})
    default double getPriceAmount() {
        return getPrice().getAmount().doubleValue();
    }

    @NonNull
    @ContextVar(path = {"price", "currency"})
    default String getPriceCurrency() {
        return getPrice().getCurrency().getName();
    }

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .set("price", getPrice())
                .copy(Trigger.super.toLocString(loc));
    }
}
