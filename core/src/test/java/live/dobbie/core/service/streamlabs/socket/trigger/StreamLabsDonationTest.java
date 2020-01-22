package live.dobbie.core.service.streamlabs.socket.trigger;

import live.dobbie.core.loc.Loc;
import live.dobbie.core.misc.Price;
import live.dobbie.core.service.streamlabs.socket.data.StreamLabsAuthor;
import live.dobbie.core.trigger.cancellable.CancellationHandler;
import live.dobbie.core.trigger.messaged.PlainMessage;
import live.dobbie.core.user.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class StreamLabsDonationTest {

    @Test
    void locNoMsgTest() {
        User user = mock(User.class);
        StreamLabsDonation d = new StreamLabsDonation(
                user,
                Instant.now(),
                new StreamLabsAuthor("foo"),
                null,
                Price.of(25, "USD"),
                null,
                mock(CancellationHandler.class)
        );
        Loc loc = new Loc();
        assertEquals("foo donated $25.00 using StreamLabs", d.toLocString(loc).build());
    }

    @Test
    void locMsgTest() {
        User user = mock(User.class);
        StreamLabsDonation d = new StreamLabsDonation(
                user,
                Instant.now(),
                new StreamLabsAuthor("foo"),
                new PlainMessage("hello, world"),
                Price.of(25, "USD"),
                null,
                mock(CancellationHandler.class)
        );
        Loc loc = new Loc();
        assertEquals("foo donated $25.00 using StreamLabs with message: \"hello, world\"", d.toLocString(loc).build());
    }

}