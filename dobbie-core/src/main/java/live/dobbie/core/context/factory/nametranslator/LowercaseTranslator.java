package live.dobbie.core.context.factory.nametranslator;

import lombok.NonNull;

public class LowercaseTranslator extends DelegateTranslator {
    public LowercaseTranslator(@NonNull VarNameTranslator translator) {
        super(translator);
    }

    @Override
    public @NonNull String translateClass(@NonNull String className) {
        return super.translateClass(className).toLowerCase();
    }

    @Override
    public @NonNull String translateMethod(@NonNull String methodName) {
        return super.translateMethod(methodName).toLowerCase();
    }
}
