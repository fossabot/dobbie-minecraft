package live.dobbie.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Jackson {
    @Getter(lazy = true)
    private final ObjectMapper instance = new ObjectMapper();
}
