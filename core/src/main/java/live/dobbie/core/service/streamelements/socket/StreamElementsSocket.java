package live.dobbie.core.service.streamelements.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.socket.client.IO;
import io.socket.client.Socket;
import live.dobbie.core.service.streamelements.events.StreamElementsEvent;
import live.dobbie.core.service.streamelements.events.StreamElementsLoyaltyStoreRedemptionEvent;
import live.dobbie.core.service.streamelements.events.StreamElementsTipEvent;
import live.dobbie.core.util.Cleanable;
import live.dobbie.core.util.logging.ILogger;
import live.dobbie.core.util.logging.Logging;
import live.dobbie.core.util.logging.PrefixLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class StreamElementsSocket implements Cleanable {
    private static final ILogger _LOGGER = Logging.getLogger(StreamElementsSocket.class);
    private final PrefixLogger logger = new PrefixLogger(_LOGGER);

    private final @NonNull String name;
    private final @NonNull StreamElementSocketCallback callback;
    private final ObjectMapper o = new ObjectMapper();

    private Socket socket;
    private String token;

    public void setToken(String token) {
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
        logger.debug("Starting socket");
        this.socket = createSocket();
    }

    Socket createSocket() {
        Socket socket;
        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"};
        try {
            socket = IO.socket("https://realtime.streamelements.com", options);
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
        setupSocket(socket);
        socket.open();
        return socket;
    }

    private void authenticate(Socket socket) {
        logger.debug("Authenticating");
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("method", "jwt");
            userInfo.put("token", token);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        socket.emit("authenticate", userInfo);
    }

    private void setupSocket(Socket socket) {
        logger.debug("Setting up event listeners");
        socket.on("connect", e -> authenticate(socket));
        socket.on("disconnect", e -> logger.debug("Disconnected: " + Arrays.toString(e)));
        socket.on("authenticated", e -> logger.debug("Authenticated: " + Arrays.toString(e)));
        socket.on("event", this::proceedEvent);
    }

    private void proceedEvent(Object[] events) {
        for (Object event : events) {
            logger.debug("Received event: " + event);
            StreamElementsEvent parsedEvent;
            try {
                parsedEvent = parseEvent(event);
            } catch (IOException | RuntimeException e) {
                logger.error("Could not parse event: " + event, e);
                continue;
            }
            callback.proceedEvent(parsedEvent);
        }
    }

    @NonNull
    private StreamElementsEvent parseEvent(Object event) throws IOException {
        JsonNode node = o.readTree(event.toString());
        if (!(node instanceof ObjectNode)) {
            throw new RuntimeException("not an object");
        }
        ObjectNode objectNode = (ObjectNode) node;
        JsonNode typeNode = objectNode.get("type");
        if (typeNode == null) {
            throw new RuntimeException("no type");
        }
        String type = typeNode.textValue();
        if (type == null) {
            throw new RuntimeException("type not a text");
        }
        switch (type) {
            case "redemption":
                return o.convertValue(node, StreamElementsLoyaltyStoreRedemptionEvent.class);
            case "tip":
                return o.convertValue(node, StreamElementsTipEvent.class);
            default:
                throw new RuntimeException("unknown type");
        }
    }

    @Override
    public void cleanup() {
        logger.debug("Cleaning up");
        stopSocket();
    }
}
