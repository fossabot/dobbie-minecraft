package live.dobbie.core.loc;

public interface LocNumericVarSelector {
    int variantCount();

    int selectVariant(Number number);
}
