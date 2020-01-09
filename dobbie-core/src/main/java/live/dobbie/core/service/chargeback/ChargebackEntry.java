package live.dobbie.core.service.chargeback;

import live.dobbie.core.misc.Price;
import live.dobbie.core.trigger.messaged.Messaged;
import live.dobbie.core.trigger.priced.Donated;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
public class ChargebackEntry {
    @NonNull Instant time;
    @NonNull String source;
    @NonNull String author;
    @NonNull Price price;
    String message;

    public static ChargebackEntry fromDonated(@NonNull Donated donated) {
        return new ChargebackEntry(
                donated.getTimestamp(),
                donated.getSource(),
                donated.getAuthor().getName(),
                donated.getPrice(),
                Messaged.getPlainMessage(donated)
        );
    }
}
