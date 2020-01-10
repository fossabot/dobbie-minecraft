package live.dobbie.core.context.factory.nametranslator;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TrailingRemovingTranslator extends DelegateTranslator {
    private final @NonNull List<String> inClass, inMethod, inField;

    public TrailingRemovingTranslator(@NonNull VarNameTranslator translator,
                                      @NonNull List<String> inClass,
                                      @NonNull List<String> inMethod,
                                      @NonNull List<String> inField) {
        super(translator);
        this.inClass = inClass;
        this.inMethod = inMethod;
        this.inField = inField;
    }

    @Override
    public @NonNull String translateClass(@NonNull String className) {
        return removeSequence(inClass, super.translateClass(className));
    }

    @Override
    public @NonNull String translateMethod(@NonNull String methodName) {
        return removeSequence(inMethod, super.translateMethod(methodName));
    }

    @Override
    public @NonNull String translateField(@NonNull String fieldName) {
        return removeSequence(inField, super.translateField(fieldName));
    }

    private static String removeSequence(List<String> list, String input) {
        String result = input;
        for (String s : list) {
            if (!result.equalsIgnoreCase(s)) {
                result = StringUtils.removeStartIgnoreCase(result, s);
                result = StringUtils.removeEndIgnoreCase(result, s);
            }
        }
        return result;
    }
}
