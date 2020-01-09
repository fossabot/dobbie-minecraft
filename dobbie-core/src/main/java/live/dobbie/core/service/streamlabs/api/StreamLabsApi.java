package live.dobbie.core.service.streamlabs.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.dobbie.core.service.Service;
import live.dobbie.core.service.ServiceUnavailableException;
import live.dobbie.core.service.SettingsBasedServiceRef;
import live.dobbie.core.service.streamlabs.StreamlabsCredentials;
import live.dobbie.core.service.streamlabs.api.data.LoyaltyPointsData;
import live.dobbie.core.service.streamlabs.api.data.StreamlabsData;
import live.dobbie.core.service.streamlabs.api.exception.NotEnoughPointsException;
import live.dobbie.core.service.streamlabs.api.exception.StreamlabsApiException;
import live.dobbie.core.user.User;
import live.dobbie.core.user.UserSettingsProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.*;

import java.io.IOException;

@RequiredArgsConstructor
public class StreamLabsApi implements Service {
    private static final String API = "https://streamlabs.com/api/v1.0/";
    public static final String NAME = "streamlabs";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final @NonNull String accessToken;

    protected LoyaltyPointsData getPoints(@NonNull String username, @NonNull String channel) throws IOException, StreamlabsApiException {
        HttpUrl.Builder urlBuilder = HttpUrl.get(API + "points").newBuilder();
        urlBuilder.addQueryParameter("access_token", accessToken);
        urlBuilder.addQueryParameter("username", username);
        urlBuilder.addQueryParameter("channel", channel);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        return executeAndExtract(request, LoyaltyPointsData.class);
    }

    // TODO issue with API fixed, subtract using special method
    public LoyaltyPointsData subtractPoints(@NonNull String username, @NonNull String channel, int amount) throws IOException, StreamlabsApiException {
        if (amount < 1) {
            throw new IllegalArgumentException();
        }
        LoyaltyPointsData pointsData = getPoints(username, channel);
        int finalPoints = pointsData.getPoints() - amount;
        if (finalPoints < 0) {
            throw new NotEnoughPointsException();
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("access_token", accessToken)
                .addFormDataPart("username", username)
                .addFormDataPart("points", String.valueOf(finalPoints))
                .build();
        Request request = new Request.Builder()
                .url(HttpUrl.get(API + "points/user_point_edit"))
                .post(requestBody)
                .build();
        return executeAndExtract(request, LoyaltyPointsData.class);
    }

    private ResponseBody execute(@NonNull Request request) throws IOException {
        return client.newCall(request).execute().body();
    }

    private <T extends StreamlabsData> T extractData(@NonNull ResponseBody body, @NonNull Class<T> clazz) throws IOException, StreamlabsApiException {
        JsonNode node = objectMapper.readTree(body.charStream());
        if (node.isTextual()) {
            throw new StreamlabsApiException(node.asText());
        }
        if (node.has("error") && node.get("error").asBoolean()) {
            throw new StreamlabsApiException(node.get("message").asText());
        }
        return objectMapper.treeToValue(node, clazz);
    }

    private <T extends StreamlabsData> T executeAndExtract(@NonNull Request request, @NonNull Class<T> clazz) throws IOException, StreamlabsApiException {
        return extractData(execute(request), clazz);
    }

    @Override
    public void cleanup() {
    }

    public static class Factory extends SettingsBasedServiceRef.ServiceFactory.Requiring<StreamLabsApi, StreamlabsCredentials> {
        public Factory() {
            super(StreamlabsCredentials.class);
        }

        @NonNull
        @Override
        protected StreamLabsApi createServiceSafe(@NonNull User user, @NonNull StreamlabsCredentials value) throws ServiceUnavailableException {
            return new StreamLabsApi(value.getToken());
        }
    }

    public static class RefFactory extends SettingsBasedServiceRef.Factory<StreamLabsApi, StreamlabsCredentials> {
        public RefFactory(@NonNull UserSettingsProvider settingsProvider) {
            super(StreamlabsCredentials.class, NAME, settingsProvider, new Factory());
        }
    }
}
