package live.dobbie.core.service.streamlabs.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.IO;
import io.socket.client.Socket;
import live.dobbie.core.exception.ParserException;
import live.dobbie.core.service.streamlabs.socket.event.DonationEvent;
import live.dobbie.core.service.streamlabs.socket.event.LoyaltyStoreRedemption;
import live.dobbie.core.service.streamlabs.socket.event.StreamLabsEvent;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.core.util.logging.PrefixLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class StreamLabsSocket implements Cleanable {
    private static final ILogger _LOGGER = Logging.getLogger(StreamLabsSocket.class);
    private final PrefixLogger logger = new PrefixLogger(_LOGGER);

    private final ObjectMapper o = new ObjectMapper();

    private final @NonNull String name;
    private final @NonNull StreamLabsSocketCallback callback;

    private Socket socket;
    private String token;

    public void updateToken(String token) {
        if (Objects.equals(this.token, token)) {
            return;
        }
        setLoggerPrefix();
        logger.info("Token updated: " + token);
        this.token = token;
        updateSocket();
    }

    private void setLoggerPrefix() {
        logger.setPrefix("[" + name + "] ");
    }

    private void updateSocket() {
        stopSocket();
        startSocket();
    }

    private void stopSocket() {
        if (this.socket == null) {
            return;
        }
        logger.debug("Stopping socket");
        this.socket.disconnect();
        this.socket = null;
    }

    private void startSocket() {
        if (this.token == null) {
            return;
        }
        logger.debug("Creating socket");
        this.socket = createSocket();
    }

    Socket createSocket() {
        Socket socket;
        try {
            socket = IO.socket("https://sockets.streamlabs.com?token=" + token);
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
        setupSocket(socket);
        logger.debug("Opening socket");
        socket.open();
        return socket;
    }

    private void setupSocket(Socket socket) {
        logger.debug("Setting up event listeners");
        socket.on("error", e -> logger.error(Arrays.toString(e)));
        socket.on("event", os -> {
            for (Object o : os) proceedEvent(o);
        });
    }

    void proceedEvent(Object object) {
        logger.debug("Received StreamLabs event: " + object);
        StreamLabsEvent event;
        try {
            event = Validate.notNull(parseEvent(object), "parseEvent");
        } catch (IOException | ParserException | RuntimeException | JSONException e) {
            logger.warning("Could not process StreamLabs event: " + e.toString());
            logger.debug("Event: " + object);
            logger.debug("Full trace:", e);
            return;
        }
        callback.proceedEvent(event);
    }

    StreamLabsEvent parseEvent(Object object) throws IOException, ParserException, JSONException {
        JSONObject jsonObject = (JSONObject) object;
        if (!jsonObject.has("type")) {
            throw new ParserException("object does not contain field \"type\"");
        }
        String type = jsonObject.getString("type");
        String content = jsonObject.toString();
        // TODO support more StreamLabs events
        Class<? extends StreamLabsEvent> cl;
        switch (type) {
            case "donation":
                cl = DonationEvent.class;
                break;
            case "loyalty_store_redemption":
                cl = LoyaltyStoreRedemption.class;
                break;
            default:
                throw new ParserException("unknown event type: " + type);
        }
        return o.readValue(content, cl);
    }

    @Override
    public void cleanup() {
        stopSocket();
    }
}
