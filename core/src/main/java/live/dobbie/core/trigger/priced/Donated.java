package live.dobbie.core.trigger.priced;

import live.dobbie.core.context.factory.ContextClass;
import live.dobbie.core.context.factory.ContextVar;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.misc.Price;
import live.dobbie.core.trigger.authored.Authored;
import live.dobbie.core.trigger.cancellable.Cancellable;
import lombok.NonNull;

@ContextClass
public interface Donated extends Authored, Priced, Cancellable {
    @NonNull Price getDonation();

    @ContextVar(path = {"donation", "amount"})
    default double getDonationAmount() {
        return getPrice().getAmount().doubleValue();
    }

    @NonNull
    @ContextVar(path = {"donation", "currency"})
    default String getDonationCurrency() {
        return getPrice().getCurrency().getName();
    }

    @Override
    default @NonNull Price getPrice() {
        return getDonation();
    }

    @NonNull
    @Override
    default LocString toLocString(@NonNull Loc loc) {
        return loc.args()
                .set("donation", getPrice().toString())
                .copy(Authored.super.toLocString(loc))
                .copy(Priced.super.toLocString(loc))
                .copy(Cancellable.super.toLocString(loc));
    }
}
