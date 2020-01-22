package live.dobbie.core.context.value;

import live.dobbie.core.misc.primitive.Primitive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public enum VarCmpCondType {
    LESS_OR_EQUAL("<=", i -> i < 1),
    GREATER_OR_EQUAL(">=", i -> i > -1),
    LESS("<", i -> i < 0),
    EQUAL("=", i -> i == 0),
    GREATER(">", i -> i > 0);

    private final @NonNull String symbols;
    private final @NonNull CompResultVerifier compResultVerifier;

    public boolean satisfies(@NonNull Primitive p0, @NonNull Primitive p1) {
        int compResult = Primitive.compare(p0, p1);
        return compResultVerifier.verify(compResult);
    }

    public static VarCmpCondType extractCondition(@NonNull String condition) {
        for (VarCmpCondType value : values()) {
            if (condition.startsWith(value.getSymbols())) {
                return value;
            }
        }
        return null;
    }

    private interface CompResultVerifier {
        boolean verify(int compResult);
    }
}
