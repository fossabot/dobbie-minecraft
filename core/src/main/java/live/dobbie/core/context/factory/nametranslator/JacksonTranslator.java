package live.dobbie.core.context.factory.nametranslator;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.NonNull;

public class JacksonTranslator extends DelegateTranslator {
    private final @NonNull PropertyNamingStrategy.PropertyNamingStrategyBase propertyNamingStrategyBase;

    public JacksonTranslator(@NonNull VarNameTranslator translator,
                             @NonNull PropertyNamingStrategy.PropertyNamingStrategyBase propertyNamingStrategyBase) {
        super(translator);
        this.propertyNamingStrategyBase = propertyNamingStrategyBase;
    }

    @Override
    public @NonNull String translateClass(@NonNull String className) {
        return translate(super.translateClass(className));
    }

    @Override
    public @NonNull String translateMethod(@NonNull String methodName) {
        return translate(super.translateMethod(methodName));
    }

    @Override
    public @NonNull String translateField(@NonNull String fieldName) {
        return translate(super.translateField(fieldName));
    }

    private String translate(String propertyName) {
        return propertyNamingStrategyBase.translate(propertyName);
    }
}
