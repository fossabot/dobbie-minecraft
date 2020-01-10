package live.dobbie.core.settings.upgrader;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import live.dobbie.core.settings.value.ISettingsValue;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaVersion implements ISettingsValue {
    public static final SchemaVersion UNKNOWN = new SchemaVersion(-1);

    @JsonProperty(value = "schema", required = true)
    int version;

    /*// plain old parser if no jackson is provided
    public static class Parser implements ISettingsParser<ISettingsSection, RootSettingsEntity, SchemaVersion> {

        @Override
        public SchemaVersion parse(ISettingsSection object, RootSettingsEntity entity, ISettingsParserContext context) throws ParserException {
            return new SchemaVersion(object.getSection("schema").getInteger());
        }
    }*/
}
