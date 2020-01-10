package live.dobbie.core.context.factory.nametranslator;

import lombok.NonNull;

public interface VarNameTranslator {
    @NonNull String translateClass(@NonNull String className);

    @NonNull String translateMethod(@NonNull String methodName);

    @NonNull String translateField(@NonNull String fieldName);

    VarNameTranslator NONE = new VarNameTranslator() {
        @Override
        public @NonNull String translateClass(@NonNull String className) {
            return className;
        }

        @Override
        public @NonNull String translateMethod(@NonNull String methodName) {
            return methodName;
        }

        @Override
        public @NonNull String translateField(@NonNull String fieldName) {
            return fieldName;
        }
    };
}
