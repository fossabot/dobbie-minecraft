package live.dobbie.core.substitutor.old.func;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.old.factory.FunctionAbstractElemFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoNameFuncElem extends NameFuncElem {
    private static final String EMPTY_NAME = "";

    public NoNameFuncElem(@NonNull Func func, @NonNull List<Substitutable> children) {
        super(func, EMPTY_NAME, children);
    }

    public static class Factory extends FunctionAbstractElemFactory<NoNameFuncElem> {
        public Factory(@NonNull Func function, int minArguments, int maxArguments) {
            super(EMPTY_NAME, function, minArguments, maxArguments);
        }

        public Factory(@NonNull Func function, int minArguments) {
            super(EMPTY_NAME, function, minArguments, minArguments);
        }

        public Factory(@NonNull Func function) {
            super(EMPTY_NAME, function);
        }

        @Override
        protected NoNameFuncElem createFunctionElement(String markerName, List<Substitutable> enclosingBlocks) {
            return new NoNameFuncElem(function, enclosingBlocks);
        }
    }
}
