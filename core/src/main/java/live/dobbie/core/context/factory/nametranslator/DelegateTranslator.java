package live.dobbie.core.context.factory.nametranslator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class DelegateTranslator implements VarNameTranslator {
    private final @NonNull
    @Delegate
    VarNameTranslator translator;
}
