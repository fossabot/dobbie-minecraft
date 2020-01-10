package live.dobbie.core.context.factory.nametranslator;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.NonNull;

public class SnakeCaseTranslator extends JacksonTranslator {
    public SnakeCaseTranslator(@NonNull VarNameTranslator translator) {
        super(translator, (PropertyNamingStrategy.PropertyNamingStrategyBase) PropertyNamingStrategy.SNAKE_CASE);
    }
}
