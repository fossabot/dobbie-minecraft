package live.dobbie.core.service.chargeback;

import com.opencsv.*;
import live.dobbie.core.misc.Price;
import live.dobbie.core.misc.currency.Currency;
import live.dobbie.core.persistence.Persistence;
import live.dobbie.core.persistence.StorageException;
import live.dobbie.core.user.User;
import live.dobbie.core.util.io.IOSupplier;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public interface ChargebackStorage extends Persistence {
    String NAME = "chargeback";

    @Override
    default @NonNull String getName() {
        return NAME;
    }

    void add(@NonNull ChargebackEntry entry) throws StorageException;

    boolean exists(@NonNull ChargebackEntry entry) throws StorageException;

    interface Factory {
        @NonNull ChargebackStorage create(@NonNull User user);
    }

    class UsingCSV implements ChargebackStorage {
        private static final ILogger LOGGER = Logging.getLogger(UsingCSV.class);

        private final @NonNull IOSupplier supplier;
        private final @NonNull CSVParser parser;
        private final @NonNull String lineSeparator;
        private final @NonNull Charset charset;
        private final @NonNull DateTimeFormatter dateTimeFormatter;
        private final ByteOrderMark bom;

        @Builder
        public UsingCSV(@NonNull IOSupplier supplier, @NonNull CSVParser parser, @NonNull String lineSeparator,
                        @NonNull Charset charset, @NonNull DateTimeFormatter dateTimeFormatter, ByteOrderMark bom) {
            this.supplier = supplier;
            this.parser = parser;
            this.lineSeparator = lineSeparator;
            this.charset = charset;
            this.dateTimeFormatter = dateTimeFormatter;
            this.bom = bom;
        }

        private final Set<ChargebackEntry> entries = new LinkedHashSet<>();
        private boolean read;

        private void readFully() throws IOException {
            entries.clear();
            try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(
                    bom == null ? supplier.input() : new BOMInputStream(supplier.input(), bom),
                    charset
            )).withCSVParser(parser).build()) {
                for (String[] s : reader) {
                    if (s.length != 6) {
                        LOGGER.warning("Skipped line \"" + Arrays.asList(s) + "\" in " + supplier);
                        continue;
                    }
                    entries.add(new ChargebackEntry(
                            Instant.from(dateTimeFormatter.parse(s[0])),
                            s[1],
                            s[2],
                            new Price(new BigDecimal(s[4]), Currency.of(s[3])),
                            s[5].isEmpty() ? null : s[5]
                    ));
                }
            } finally {
                read = true;
            }
        }

        private void flushFully() throws IOException {
            try (OutputStream output = supplier.output();
                 ICSVWriter writer = new CSVWriterBuilder(new OutputStreamWriter(output, charset)).withParser(parser).withLineEnd(lineSeparator).build()
            ) {
                if (bom != null) {
                    output.write(bom.getBytes());
                }
                for (ChargebackEntry entry : entries) {
                    writer.writeNext(new String[]{
                            dateTimeFormatter.format(entry.getTime()),
                            entry.getSource(),
                            entry.getAuthor(),
                            entry.getPrice().getCurrency().getName(),
                            String.valueOf(entry.getPrice().getAmount()),
                            entry.getMessage() == null ? "" : entry.getMessage()
                    });
                }
            }
        }

        private void read() throws StorageException {
            if (!read) {
                try {
                    readFully();
                } catch (IOException e) {
                    throw new StorageException(e);
                }
            }
        }

        private void flush() throws StorageException {
            try {
                flushFully();
            } catch (IOException e) {
                throw new StorageException(e);
            }
        }

        @Override
        public void add(@NonNull ChargebackEntry entry) throws StorageException {
            read();
            entries.add(entry);
            flush();
        }

        @Override
        public boolean exists(@NonNull ChargebackEntry entry) throws StorageException {
            read();
            return entries.contains(entry);
        }

        @Override
        public void cleanup() {
            entries.clear();
        }

        public static UsingCSV excelFriendly(@NonNull IOSupplier supplier, @NonNull ZoneId timezone) {
            return builder()
                    .supplier(supplier)
                    .parser(new CSVParserBuilder().withSeparator(';').build())
                    .lineSeparator("\r\n")
                    .charset(StandardCharsets.UTF_8)
                    .bom(ByteOrderMark.UTF_8)
                    .dateTimeFormatter(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(timezone))
                    .build();
        }

        public static UsingCSV excelFriendly(@NonNull IOSupplier supplier) {
            return excelFriendly(supplier, ZoneId.of("UTC"));
        }
    }
}
