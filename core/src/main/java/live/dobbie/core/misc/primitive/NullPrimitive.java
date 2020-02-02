package live.dobbie.core.misc.primitive;

public class NullPrimitive implements Primitive {
    public static final NullPrimitive INSTANCE = new NullPrimitive();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullPrimitive;
    }

    @Override
    public int hashCode() {
        if (this == INSTANCE) {
            return super.hashCode();
        }
        return INSTANCE.hashCode();
    }

    @Override
    public Object getValue() {
        return null;
    }
}
