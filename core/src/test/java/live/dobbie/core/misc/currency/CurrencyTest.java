package live.dobbie.core.misc.currency;

import live.dobbie.core.config.DobbieLocale;
import live.dobbie.icu.util.ULocale;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyTest {

    @Test
    void usdTest() {
        DobbieLocale locale = mock(DobbieLocale.class);
        when(locale.getLocale()).thenReturn(ULocale.US);

        Currency usd = Currency.of("USD");
        assertNotNull(usd);
        assertEquals("$1.00", usd.format(1, locale));
    }

    @Test
    void rubTest() {
        ULocale ru_RU = ULocale.forLanguageTag("ru-RU");
        DobbieLocale locale = mock(DobbieLocale.class);
        when(locale.getLocale()).thenReturn(ru_RU);

        Currency rub = Currency.of("RUB");
        assertNotNull(rub);
        assertEquals("1,00 ₽", rub.format(1, locale));
    }

    @Test
    void anyCaseTest() {
        Currency UsD = Currency.of("UsD");
        Currency USD = Currency.of("USD");
        assertSame(UsD, USD);
    }

}