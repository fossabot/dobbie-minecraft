package live.dobbie.core.service.streamlabs.api.exception;

public class StreamlabsApiException extends Exception {
    public StreamlabsApiException() {
    }

    public StreamlabsApiException(String message) {
        super(message);
    }

    public StreamlabsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
