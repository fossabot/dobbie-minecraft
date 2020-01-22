package live.dobbie.core.service.streamlabs.socket;

import live.dobbie.core.service.streamlabs.api.StreamLabsApi;
import live.dobbie.core.service.streamlabs.api.exception.StreamlabsApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;

class StreamLabsSocketTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "streamlabs-test", matches = "true")
    void realTest() throws IOException, StreamlabsApiException, InterruptedException {
        StreamLabsSocket socket = new StreamLabsSocket("test", System.out::println);
        socket.updateToken(new StreamLabsApi(System.getenv("streamlabs-test-token")).getSocketToken());
        Thread.sleep(Long.parseLong(System.getenv("streamlabs-test-duration")));
    }

}