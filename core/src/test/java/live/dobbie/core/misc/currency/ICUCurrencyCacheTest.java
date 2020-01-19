package live.dobbie.core.misc.currency;

import live.dobbie.icu.util.Currency;
import org.junit.jupiter.api.Test;

import static live.dobbie.core.misc.currency.ICUCurrencyCache.get;
import static org.junit.jupiter.api.Assertions.*;

class ICUCurrencyCacheTest {

    @Test
    void basicTest() {
        Currency usd = get("USD");
        assertNotNull(usd);
        assertSame(get("USD"), usd);
    }

    @Test
    void failTest() {
        Currency usd = get("usd");
        assertNull(usd);
    }

}