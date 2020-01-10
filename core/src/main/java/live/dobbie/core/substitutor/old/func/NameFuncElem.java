package live.dobbie.core.substitutor.old.func;

import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.environment.Env;
import live.dobbie.core.substitutor.old.factory.FunctionAbstractElemFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NameFuncElem extends FuncElem {
    @NonNull String name;
    @NonNull List<Substitutable> children;

    public NameFuncElem(@NonNull Func func, @NonNull String name, @NonNull List<Substitutable> children) {
        super(func);
        this.name = name;
        this.children = children;
    }

    @Override
    protected List<String> arguments(@NonNull Env env) {
        return children.stream().map(elem -> elem.substitute(env)).collect(Collectors.toList());
    }

    public static class Factory extends FunctionAbstractElemFactory<NameFuncElem> {
        public Factory(@NonNull String markerName, @NonNull Func function, int minArguments, int maxArguments) {
            super(markerName, function, minArguments, maxArguments);
        }

        public Factory(@NonNull String markerName, @NonNull Func function, int minArguments) {
            super(markerName, function, minArguments, minArguments);
        }

        public Factory(@NonNull String markerName, @NonNull Func function) {
            super(markerName, function);
        }

        @Override
        protected NameFuncElem createFunctionElement(String markerName, List<Substitutable> enclosingBlocks) {
            return new NameFuncElem(function, markerName, enclosingBlocks);
        }
    }
}
