package live.dobbie.core.config;

import com.ibm.icu.util.ULocale;
import live.dobbie.core.misc.Price;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PriceFormattingTest {

    @Test
    void basicTest() {
        DobbieLocale locale = mock(DobbieLocale.class);
        PriceFormatting priceFormatting = new PriceFormatting(Collections.singletonMap("USD", "{amount, plural, =300 {THREE HUNDRED BUCKS} other {{amount} dallaz}}"));
        assertEquals("THREE HUNDRED BUCKS", priceFormatting.format(Price.of(300, "usd"), locale));
    }

    @Test
    void noMapTest() {
        DobbieLocale locale = mock(DobbieLocale.class);
        PriceFormatting priceFormatting = new PriceFormatting(Collections.emptyMap());
        assertNull(priceFormatting.format(Price.of(300, "usd"), locale));
    }

    @Test
    void staticFormatNoMapTest() {
        DobbieLocale locale = mock(DobbieLocale.class);
        when(locale.getLocale()).thenReturn(ULocale.US);
        when(locale.getDefaultCurrencyPattern()).thenReturn("lolwut");
        assertEquals("$300.00", PriceFormatting.format(Price.of(300, "usd"), locale, null));
        assertEquals("lolwut", PriceFormatting.format(Price.of(300, "nonsense"), locale, null));
    }

}