package live.dobbie.core.service.twitch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import live.dobbie.core.config.LoggingConfig;
import live.dobbie.core.misc.Range;
import live.dobbie.core.misc.currency.Currency;
import live.dobbie.core.settings.source.jackson.JacksonParseable;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TwitchSettings {

    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JacksonParseable({"services", "twitch"})
    public static class Global implements ISettingsValue {
        @NonNull
        final Client client;

        @Value
        @AllArgsConstructor(onConstructor = @__(@JsonCreator))
        public static class Client {
            @NonNull
            final String login;
            @NonNull
            final String token;
        }
    }

    @Value
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JacksonParseable({"services", "twitch"})
    public static class Player implements ISettingsValue {
        final boolean enabled;
        @NonNull
        final String channel;
        @NonNull
        final LoggingConfig logging;
        @NonNull
        final Events events;

        @JsonCreator
        public Player(@JsonProperty(value = "enabled", required = true) boolean enabled,
                      @JsonProperty(value = "channel", required = true) @NonNull String channel,
                      @NonNull @JsonProperty(value = "logging", required = true) LoggingConfig logging,
                      @NonNull @JsonProperty(value = "events", required = true) Events events) {
            this.enabled = enabled;
            this.channel = channel;
            this.logging = logging;
            this.events = events;
        }
    }

    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Events {
        @NonNull
        final SubscriptionEventConfig subscription;
        @NonNull
        final SubscriptionEventConfig giftSubscription;
        //@NonNull final EventConfig streamStarted;
        //@NonNull final EventConfig streamStopped;
        //@NonNull final EventConfig viewerJoined;
        //@NonNull final EventConfig viewerLeft;
        @NonNull
        final EventConfig raid;
        @NonNull
        final EventConfig host;
        @NonNull
        final EventConfig follow;
        @NonNull
        final EventConfig cheer;
        @NonNull
        final EventConfig chat;
        //@NonNull final EventConfig channelPoints;
        //@NonNull final CommandEventConfig command;

        @Data
        @Setter(AccessLevel.PRIVATE)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public static class EventConfig {
            final String destination;
            final boolean enabled;

            @JsonCreator
            public EventConfig(@JsonProperty(value = "destination") String destination,
                               @JsonProperty(value = "enabled", required = true) boolean enabled) {
                this.destination = destination;
                this.enabled = enabled;
            }
        }

        @Value
        @EqualsAndHashCode(callSuper = true)
        @ToString(callSuper = true)
        public static class SubscriptionEventConfig extends EventConfig {
            @NonNull
            final Tiers tiers;

            @JsonCreator
            public SubscriptionEventConfig(@JsonProperty("destination") String destination,
                                           @JsonProperty(value = "enabled", required = true) boolean enabled,
                                           @JsonProperty(value = "tiers", required = true) @NonNull Tiers tiers) {
                super(destination, enabled);
                this.tiers = tiers;
            }

            @Value
            @AllArgsConstructor(onConstructor = @__(@JsonCreator))
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            public static class Tiers {
                @NonNull
                final TierConfig twitchPrime;
                @NonNull
                final TierConfig tier1;
                @NonNull
                final TierConfig tier2;
                @NonNull
                final TierConfig tier3;
            }

            @Value
            public static class TierConfig {
                final String destination;
                final boolean enabled;

                @JsonCreator
                public TierConfig(@JsonProperty(value = "destination") String destination,
                                  @JsonProperty(value = "enabled", required = true) boolean enabled) {
                    this.destination = destination;
                    this.enabled = enabled;
                }
            }
        }

        @Value
        @ToString(callSuper = true)
        @AllArgsConstructor(onConstructor = @__(@JsonCreator))
        public static class CommandEventConfig {
            @NonNull List<CommandInfo> list;

            @Value
            public static class CommandInfo {
                @NonNull String name;
                List<String> aliases;
                String description;
                PriceRangeList price;
                @NonNull ArgumentConfig arguments;
                Map<String, String> responses;

                @JsonCreator
                public CommandInfo(@NonNull @JsonProperty(value = "name", required = true) String name,
                                   @JsonProperty("aliases") List<String> aliases,
                                   @JsonProperty("description") String description,
                                   @JsonProperty("price") TwitchSettings.Events.CommandEventConfig.CommandInfo.PriceRangeList price,
                                   @NonNull @JsonProperty(value = "arguments", required = true) ArgumentConfig arguments,
                                   @JsonProperty(value = "responses") Map<String, String> responses) {
                    this.name = name;
                    this.aliases = aliases;
                    this.description = description;
                    this.price = price;
                    this.arguments = arguments;
                    this.responses = responses;
                }

                @Value
                @JsonDeserialize(using = PriceRangeList.Deserializer.class)
                public static class PriceRangeList {
                    @NonNull Currency currency;
                    @NonNull List<Range> ranges;

                    public static class Deserializer extends JsonDeserializer<PriceRangeList> {
                        @Override
                        public PriceRangeList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                            if (p.nextToken() != JsonToken.FIELD_NAME) {
                                throw new IllegalArgumentException("expected currency name");
                            }
                            Currency currency = Currency.of(p.currentName());
                            PriceRangeList price;
                            switch (p.nextValue()) {
                                case START_ARRAY:
                                    // price list
                                    List<Range> rangeList = new ArrayList<>();
                                    loop:
                                    do {
                                        switch (p.nextValue()) {
                                            case VALUE_NUMBER_INT:
                                            case VALUE_NUMBER_FLOAT:
                                                rangeList.add(new Range(p.getDoubleValue()));
                                                break;
                                            case END_ARRAY:
                                                if (rangeList.isEmpty()) {
                                                    throw new IllegalArgumentException("range list is empty");
                                                }
                                                price = new PriceRangeList(currency, Collections.unmodifiableList(rangeList));
                                                break loop;
                                            default:
                                                throw new IllegalArgumentException("unexpected token when parsing range list: " + p.currentToken());
                                        }
                                    } while (true);
                                    break;
                                case VALUE_NUMBER_INT:
                                case VALUE_NUMBER_FLOAT:
                                    price = new PriceRangeList(currency, Collections.singletonList(new Range(p.getDoubleValue())));
                                    break;
                                case VALUE_STRING:
                                    price = new PriceRangeList(currency, Collections.singletonList(Range.parse(p.getText())));
                                    break;
                                default:
                                    throw new IllegalArgumentException("unexpected token when tried to parse currency list: " + p.nextToken());
                            }
                            p.nextToken();
                            return price;
                        }
                    }
                }

                @Value
                @AllArgsConstructor(onConstructor = @__(@JsonCreator))
                public static class ArgumentConfig {
                    @NonNull PerArgumentConfig amount;
                    @NonNull PerArgumentConfig message;

                    @JsonDeserialize(using = PerArgumentConfig.Deserializer.class)
                    public enum PerArgumentConfig {
                        NONE,
                        ENABLED,
                        REQUIRED;

                        public static class Deserializer extends JsonDeserializer<PerArgumentConfig> {
                            @Override
                            public PerArgumentConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                                String raw = Validate.notNull(p.getText(), "no text value").toUpperCase();
                                PerArgumentConfig value;
                                try {
                                    value = PerArgumentConfig.valueOf(raw);
                                } catch (IllegalArgumentException notFound) {
                                    throw new IllegalArgumentException("unknown value: " + raw);
                                }
                                return value;
                            }
                        }
                    }
                }
            }
        }
    }
}
