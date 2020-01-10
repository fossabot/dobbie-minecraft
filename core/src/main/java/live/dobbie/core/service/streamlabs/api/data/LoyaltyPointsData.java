package live.dobbie.core.service.streamlabs.api.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoyaltyPointsData implements StreamlabsData {
    private int id;
    private String platform;
    private String channel;
    private String username;
    private int exp;
    private int points;
    private String ta_id;
    private String status;
    private int time_watched;
    private String created_at;
    private String updated_at;
}
