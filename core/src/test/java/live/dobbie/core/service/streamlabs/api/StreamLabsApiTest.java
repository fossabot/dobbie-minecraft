package live.dobbie.core.service.streamlabs.api;

import live.dobbie.core.service.streamlabs.api.data.LoyaltyPointsData;
import live.dobbie.core.service.streamlabs.api.exception.StreamlabsApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;

class StreamLabsApiTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "streamlabs-test", matches = "true")
    void testGetPoints() throws IOException, StreamlabsApiException {
        StreamLabsApi streamLabsApi = new StreamLabsApi(System.getenv("streamlabs-test-token"));
        LoyaltyPointsData points = streamLabsApi.getPoints(System.getenv("streamlabs-test-target-user"), System.getenv("streamlabs-test-user"));
        System.out.println("---");
        System.out.println(points);
        System.out.println("---");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "streamlabs-test", matches = "true")
    void testSubtractPoints() throws IOException, StreamlabsApiException {
        StreamLabsApi streamLabsApi = new StreamLabsApi(System.getenv("streamlabs-test-token"));
        LoyaltyPointsData points = streamLabsApi.subtractPoints(System.getenv("streamlabs-test-target-user"), System.getenv("streamlabs-test-user"), Long.parseLong(System.getenv("streamlabs-test-target-user-subtract")));
        System.out.println("---");
        System.out.println(points);
        System.out.println("---");
    }

}