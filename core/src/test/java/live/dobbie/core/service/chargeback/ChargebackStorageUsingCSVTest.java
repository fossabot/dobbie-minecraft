package live.dobbie.core.service.chargeback;

import com.opencsv.CSVParser;
import live.dobbie.core.exception.StorageException;
import live.dobbie.core.misc.Price;
import live.dobbie.core.misc.currency.Currency;
import live.dobbie.core.util.io.StringSupplier;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChargebackStorageUsingCSVTest {
    private static final DateTimeFormatter ISO_UTC_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("UTC"));
    private static final String UTF8_BOM = "\uFEFF";

    @Test
    void basicReadTest() throws StorageException {
        ChargebackStorage.UsingCSV storage = new ChargebackStorage.UsingCSV(
                new StringSupplier("2000-01-01T00:00:00Z,testSource,testAuthor,UAH,40.00,на штани\n", StandardCharsets.UTF_8),
                new CSVParser(),
                "\n",
                StandardCharsets.UTF_8,
                ISO_UTC_FORMATTER,
                null);
        Instant time = Instant.parse("2000-01-01T00:00:00Z");
        ChargebackEntry entry = new ChargebackEntry(time, "testSource", "testAuthor", new Price(new BigDecimal("40.00"), Currency.of("UAH")), "на штани");
        assertTrue(storage.exists(entry));
    }

    @Test
    void basicWriteTest() throws StorageException {
        StringSupplier source = new StringSupplier("", StandardCharsets.UTF_8);
        ChargebackStorage.UsingCSV storage = new ChargebackStorage.UsingCSV(
                source,
                new CSVParser(),
                "\n",
                StandardCharsets.UTF_8,
                ISO_UTC_FORMATTER,
                null);
        Instant time = Instant.parse("2000-01-01T00:00:00Z");
        ChargebackEntry entry = new ChargebackEntry(time, "testSource", "testAuthor", new Price(new BigDecimal("40.00"), Currency.of("UAH")), "на штани");
        storage.add(entry);
        assertEquals("\"2000-01-01T00:00:00Z\",\"testSource\",\"testAuthor\",\"UAH\",\"40.00\",\"на штани\"\n", source.getStr());
    }

    @Test
    void excelFriendlyReadTest() throws StorageException {
        ChargebackStorage.UsingCSV storage = ChargebackStorage.UsingCSV.excelFriendly(
                new StringSupplier("2000-01-01T00:00:00Z;testSource;testAuthor;UAH;40.00;\n", StandardCharsets.UTF_8)
        );
        Instant time = Instant.parse("2000-01-01T00:00:00Z");
        ChargebackEntry entry = new ChargebackEntry(time, "testSource", "testAuthor", new Price(new BigDecimal("40.00"), Currency.of("UAH")), null);
        assertTrue(storage.exists(entry));
    }

    @Test
    void excelFriendlyWriteTest() throws StorageException, FileNotFoundException {
        StringSupplier source = new StringSupplier("", StandardCharsets.UTF_8);
        ChargebackStorage.UsingCSV storage = ChargebackStorage.UsingCSV.excelFriendly(source);
        Instant time = Instant.parse("2000-01-01T00:00:00Z");
        ChargebackEntry entry = new ChargebackEntry(time, "testSource", "testAuthor", new Price(new BigDecimal("40.00"), Currency.of("UAH")), "на штани");
        storage.add(entry);
        String expected = UTF8_BOM + "\"2000-01-01T00:00:00Z\";\"testSource\";\"testAuthor\";\"UAH\";\"40.00\";\"на штани\"\r\n";
        assertEquals(expected, source.getStr());
    }

}